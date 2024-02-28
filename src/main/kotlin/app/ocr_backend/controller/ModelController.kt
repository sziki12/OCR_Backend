package app.ocr_backend.controller

import java.io.File
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit


class ModelController {
    var builder = ProcessBuilder()

    fun processImage()
    {

        builder.command("cmd.exe", "/c", "dir")

        builder.directory(File(System.getProperty("user.home")))
        val process = builder.start()
        val streamGobbler = StreamHandler(process.inputStream, System.out::println)
        //val future: Future<*> = executorService.submit(streamGobbler)

        val exitCode = process.waitFor()

        //assertDoesNotThrow { future[10, TimeUnit.SECONDS] }
        //assertEquals(0, exitCode)
    }
}