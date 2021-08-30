package com.github.kdm1jkm.jwebtoon.webtoontask

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger

internal typealias DownloadImageFinishHandler = (max: Int, count: Int, num: Int) -> Unit

abstract class AbstractWebtoonTask {
    companion object {
        private const val DEFAULT_THREAD_COUNT = 3
        private const val USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36"
    }

    protected abstract val imageQueue: ConcurrentLinkedQueue<URL>

    private val targetDirectory = lazy {
        File("download/$this")
    }
    var threadCount = DEFAULT_THREAD_COUNT
    var onEachTaskFinished: DownloadImageFinishHandler? = null

    val imageCount
        get() = imageQueue.size

    abstract override fun toString(): String

    fun run() {
        if (targetDirectory.value.isFile) targetDirectory.value.delete()
        if (!targetDirectory.value.exists())
            targetDirectory.value.mkdirs()

        val semaphore = Semaphore(threadCount)

        val filenameCount = AtomicInteger(0)
        val finished = AtomicInteger(0)

        val max = imageQueue.size

        runBlocking {
            while (!imageQueue.isEmpty()) {
                val address = imageQueue.poll()
                val extension = address.toString().split(".").last()
                val file = File(targetDirectory.value, "%04d.$extension".format(filenameCount.addAndGet(1)))
                val count = filenameCount.get()

                launch {
                    semaphore.withPermit {
                        val connection = address.openConnection()
                        connection.setRequestProperty("User-agent", USER_AGENT)

                        val fout = FileOutputStream(file)
                        val client = HttpClient
                            .newBuilder()
                            .build()

                        val data = client.send(
                            HttpRequest.newBuilder(
                                address.toURI()
                            ).GET().header("User-agent", USER_AGENT).build(),
                            HttpResponse.BodyHandlers.ofByteArray()
                        ).body()

                        fout.write(data)
                        fout.close()

                        onEachTaskFinished?.let { it(max, finished.addAndGet(1), count) }
                    }
                }
            }
        }
    }
}

