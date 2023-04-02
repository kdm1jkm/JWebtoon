import java.io.InputStreamReader
import java.net.URL
import java.net.URLEncoder

fun main(args: Array<String>) {
    println("Hello World!")

    val searchString = "청춘 블"
    val searchURL = getSearchUrl(searchString)

    val connection = searchURL.openConnection()
    connection.setRequestProperty(
        "User-Agent",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/111.0"
    )

    val reader = InputStreamReader(connection.getInputStream())
    println(reader.readText())
}

private fun getSearchUrl(searchString: String): URL =
    URL("https://comic.naver.com/search?keyword=${URLEncoder.encode(searchString, "UTF-8")}")