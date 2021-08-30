package com.github.kdm1jkm.jwebtoon.webtoon

import com.github.kdm1jkm.jwebtoon.webtoontask.AbstractWebtoonTask
import java.net.URL

interface Webtoon {
    val webtoonName: String

    val author: String?
    val detailInfo: String?
    val genre: String?
    val url: URL
    val thumbnailUrl: URL?

    fun getEveryTask(): List<AbstractWebtoonTask>?
    fun getTaskByNo(no: Int): AbstractWebtoonTask
}