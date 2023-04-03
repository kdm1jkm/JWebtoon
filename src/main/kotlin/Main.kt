import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import org.jsoup.Jsoup
import java.io.FileOutputStream
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {
    val playwright = Playwright.create()
    val browser = playwright.firefox().launch()
    val page = browser.newPage()
    val executor = Executors.newCachedThreadPool()

    downloadWholeWebtoon(page, executor, 570503, 313..439)

    executor.shutdown()
    executor.awaitTermination(10, TimeUnit.SECONDS)

    playwright.close()
}

private fun downloadWholeWebtoon(
    page: Page,
    executor: ExecutorService,
    titleId: Int,
    episodeRange: IntRange
) {
    for (episode in episodeRange) {
        downloadEpisode(page, executor, titleId, episode)
    }
}

private fun downloadEpisode(
    page: Page,
    executor: ExecutorService,
    titleId: Int,
    episode: Int
) {
    val webtoonPageInfo = getWebtoonPageInfo(page, titleId, episode)
    val directory = "download/${webtoonPageInfo.name}/[${episode}] ${webtoonPageInfo.title}"
    Files.createDirectories(Paths.get(directory))

    for ((i, image) in webtoonPageInfo.images.withIndex()) {
        executor.execute {
            val file = FileOutputStream("${directory}/${i + 1}.jpg")
            val connection = URL(image).openConnection()
            connection.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/111.0"
            )
            val download = connection.getInputStream()
            download.transferTo(file)
            file.close()
            download.close()

            println("\r[downloaded] ${directory}/${i + 1}.jpg")
        }
    }

}

data class WebtoonInfo(val totalEpisodeCount: Int, val name: String)
data class WebtoonPageInfo(val images: List<String>, val title: String, val name: String)


fun replaceInvalidCharacter(inputString: String) = inputString
    .replace("\\", "")
    .replace("/", "")
    .replace(":", "")
    .replace("*", "")
    .replace("?", "")
    .replace("\"", "")
    .replace("<", "")
    .replace(">", "")
    .replace("|", "")

private fun getWebtoonPageInfo(
    page: Page,
    titleId: Int,
    episode: Int
): WebtoonPageInfo {
    val webtoonPageUrl = "https://comic.naver.com/webtoon/detail?titleId=${titleId}&no=${episode}"
    page.navigate(webtoonPageUrl)
    page.waitForSelector("#sectionContWide")
    val document = Jsoup.parse(page.content())
    val images = document.select("#sectionContWide > img").map {
        it.attributes()["src"]
    }
    val title = replaceInvalidCharacter(document.select("#subTitle_toolbar").text())
    val name = document.title().replace(" :: 네이버웹툰", "")
    return WebtoonPageInfo(images, title, name)
}

private fun getWebtoonInfo(page: Page, titleId: Int): WebtoonInfo {
    val webtoonListUrl = "https://comic.naver.com/webtoon/list?titleId=${titleId}"

    page.navigate(webtoonListUrl)
    page.waitForSelector(".EpisodeListView__count--fTMc5")

    val document = Jsoup.parse(page.content())

    val totalEpisodeCount = document
        .selectXpath("/html/body/div[1]/div/div[2]/div/div[1]/div[3]/div[1]/div[1]")
        .text()
        .replace("총 ", "")
        .replace("화", "")
        .toInt()

    val name = replaceInvalidCharacter(document.title().replace(" :: 네이버웹툰", ""))

    return WebtoonInfo(totalEpisodeCount, name)
}
