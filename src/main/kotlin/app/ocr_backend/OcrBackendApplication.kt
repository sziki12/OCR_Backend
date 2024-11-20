package app.ocr_backend

import app.ocr_backend.security.config.RsaKeyProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@EnableConfigurationProperties(RsaKeyProperties::class)
@SpringBootApplication
class OcrBackendApplication

fun main(args: Array<String>) {
    runApplication<OcrBackendApplication>(*args)
}
