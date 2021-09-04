package com.github.kdm1jkm.jwebtoon.webtoon

import com.github.kdm1jkm.jwebtoon.Util.replaceSpecialCharacter
import com.github.kdm1jkm.jwebtoon.webtoontask.AbstractWebtoonTask
import com.github.kdm1jkm.jwebtoon.webtoontask.NaverWebtoonTask
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URL
import java.net.URLEncoder
import java.util.regex.Pattern
import kotlin.streams.toList


class NaverWebtoon private constructor(
    private val id: Int,
    override val webtoonName: String,
    override val url: URL,
    private val doc: Document,
) : Webtoon {


    override var author: String? = null
        private set
    override var detailInfo: String? = null
        private set
    override var genre: String? = null
        private set
    override var thumbnailUrl: URL? = null
        private set

    override fun getEveryTask(): List<AbstractWebtoonTask>? {
        val link = doc.selectFirst("td.title > a")
        val attHref = link?.attr("href") ?: return null
        val href = URL("https://comic.naver.com${attHref}")
        val lastEp = Pattern.compile("&?no=(\\d+)").matcher(href.query).run {
            find()
            group(1)
        }.run {
            toIntOrNull()
        } ?: return null

        val result = ArrayList<NaverWebtoonTask>()
        for (i in 1..lastEp) {
            result.add(NaverWebtoonTask(id, i, this@NaverWebtoon))
        }
        return result

    }

    override fun getTaskByNo(no: Int): AbstractWebtoonTask = NaverWebtoonTask(id, no, this)

    companion object {
        fun load(webtoonName: String): NaverWebtoon? = URLEncoder.encode(webtoonName, "utf-8").let {
            Jsoup.connect("https://comic.naver.com/search.nhn?m=webtoon&keyword=$it").get()
        }.run {
            selectFirst("#content > div:nth-child(2) > ul > li > h5 > a")
        }.also {
            if (it == null) return null
        }!!.run {
            attr("href")
        }.let {
            Pattern.compile("&?titleId=(\\d+)").matcher(it).run {
                find()
                group(1)
            }
        }.also {
            if (it == null) return null
        }.run {
            toIntOrNull()
        }.also {
            if (it == null) return null
        }.let {
            load(it!!)
        }

        fun load(id: Int): NaverWebtoon {
            val url = "https://comic.naver.com/webtoon/list?titleId=$id"
            val webtoonDoc = Jsoup.connect(url).get()

            val name = getWebtoonName(webtoonDoc)

            val author = getWebtoonAuthor(webtoonDoc)
            val detailInfo = getWebtoonDetailInfo(webtoonDoc)
            val genre = getWebtoonGenre(webtoonDoc)
            val thumbnailUrl = getWebtoonThumbnailUrl(webtoonDoc)

            return NaverWebtoon(id, name, URL(url), webtoonDoc).apply {
                this.author = author
                this.detailInfo = detailInfo
                this.genre = genre
                this.thumbnailUrl = thumbnailUrl
            }
        }

        fun search(keyWord: String): List<Pair<String, Int>>? {
            val encodedName = URLEncoder.encode(keyWord, "utf-8")
            val searchUrl = "https://comic.naver.com/search.nhn?m=webtoon&keyword=$encodedName"
            val searchDoc = Jsoup.connect(searchUrl).get()

            val links = searchDoc.select("#content > div:nth-child(2) > ul > li > h5 > a") ?: return null

            return links.stream().map { link ->
                val href = link.attr("href")
                val titleId = href.let {
                    Pattern.compile("&?titleId=(\\d+)").matcher(it).run {
                        find()
                        group(1)
                    }
                }.run {
                    toInt()
                }
                return@map Pair(link.ownText(), titleId)
            }.toList()
        }


        private fun getWebtoonName(doc: Document): String {
            return doc.title().run {
                substring(0, length - 10)
            }.replaceSpecialCharacter()
        }

        private fun getWebtoonAuthor(doc: Document): String? {
            return doc.selectFirst("#content > div.comicinfo > dic.detail > h2 > span")?.ownText()?.trim()
        }

        private fun getWebtoonDetailInfo(doc: Document): String? {
            return doc.selectFirst("div.detail > p")?.ownText()?.trim()
        }

        private fun getWebtoonGenre(doc: Document): String? {
            return doc.selectFirst("span.genre")?.ownText()
        }

        private fun getWebtoonThumbnailUrl(doc: Document): URL? {
            return doc.selectFirst("#content > div > div > a > img")?.attr("src")?.let {
                URL(it)
            }
        }
        // holy shit! - 도현
    }

    override fun toString(): String = "[NaverWebtoon]$author-$webtoonName($id)"


}
