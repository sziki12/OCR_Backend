package app.ocr_backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OcrBackendApplication

fun main(args: Array<String>) {
    runApplication<OcrBackendApplication>(*args)
}
