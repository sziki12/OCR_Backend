package app.ocr_backend.ai.categorisation.backend_dto

data class CategoriseParams(
    val categorise_model:String,
    val items: String,
    val categories: String
)