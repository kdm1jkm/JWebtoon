package com.github.kdm1jkm.jwebtoon.main

import com.github.kdm1jkm.jwebtoon.webtoon.NaverWebtoon

fun main() {
    NaverWebtoon
        .load("갓 오브 하이스쿨")!!
        .getEveryTask()!!
        .stream().limit(5)
        .forEach { task ->
            task.apply {
                onEachTaskFinished = { max, count, _ -> print("\r$task - $count/$max") }
                threadCount = 10
            }.run()
            println()
        }
}
