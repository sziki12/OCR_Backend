package app.ocr_backend.legacy

import java.io.BufferedReader

import java.io.InputStream
import java.io.InputStreamReader
import java.util.function.Consumer


class StreamHandler(private val inputStream:InputStream, private val consumer:Consumer<String>):Runnable  {
    override fun run() {
        BufferedReader(InputStreamReader(inputStream)).lines()
            .forEach(consumer)
    }
}