package com.github.kdm1jkm.jwebtoon.webtoontask

import com.github.kdm1jkm.jwebtoon.webtoon.NaverWebtoon
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URL
import java.util.concurrent.ConcurrentLinkedQueue

class NaverWebtoonTask(private val id: Int, private val no: Int, private val parent: NaverWebtoon) :
    AbstractWebtoonTask() {

    private val url: URL = URL("https://comic.naver.com/webtoon/detail?titleId=$id&no=$no")
    override val imageQueue: ConcurrentLinkedQueue<URL> by lazy {
        ConcurrentLinkedQueue(getWebtoonImageSource(Jsoup.connect(url.toString()).get()))
    }

    private fun getWebtoonImageSource(doc: Document): List<URL> =
        doc.select("img[alt='comic content']").stream().map {
            URL(it.attr("src"))
        }.toList()

    override fun toString(): String = "${parent.webtoonName}($id)/%04d".format(no)
}