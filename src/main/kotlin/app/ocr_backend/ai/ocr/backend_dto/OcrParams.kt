package app.ocr_backend.ai.ocr.backend_dto

data class OcrParams(
    val image:String,
    val path:String,
    val ocr_type:String,
    val orientation:String,
    val parse_model:String,
)
