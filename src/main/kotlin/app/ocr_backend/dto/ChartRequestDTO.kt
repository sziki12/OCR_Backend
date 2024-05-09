package app.ocr_backend.dto

import java.time.LocalDate

class ChartRequestDTO(
    val from:LocalDate?,
    val to:LocalDate?,
    val type:String
) {
}