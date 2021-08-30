package com.github.kdm1jkm.jwebtoon

internal object Util {
    internal fun String.replaceSpecialCharacter(): String = this
        .replace("\\", "＼")
        .replace("/", "／")
        .replace(":", "：")
        .replace("*", "＊")
        .replace("?", "？")
        .replace("\"", "＂")
        .replace("<", "＜")
        .replace(">", "＞")
        .replace("|", "｜")
}