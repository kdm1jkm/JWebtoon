import java.net.URL
import java.net.URLEncoder

fun main(args: Array<String>) {
    println("Hello World!")

    val searchString = "청춘 블"
    val searchURL = getSearchUrl(searchString)
}

private fun getSearchUrl(searchString: String): URL =
    URL("https://comic.naver.com/search?keyword=${URLEncoder.encode(searchString, "UTF-8")}")