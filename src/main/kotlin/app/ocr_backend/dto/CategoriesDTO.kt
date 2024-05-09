package app.ocr_backend.dto

data class CategoriesDTO(
    val Housing:List<String>?,
    val Clothing:List<String>?,
    val Food:List<String>?,
    val Personal:List<String>?,
    val Utilities:List<String>?,
    val Household:List<String>?,
    val Entertainment:List<String>?,
    val Other:List<String>?) {
}