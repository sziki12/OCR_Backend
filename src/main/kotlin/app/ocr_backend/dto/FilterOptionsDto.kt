package app.ocr_backend.dto

data class FilterOptionsDto(
    val placeNames : MutableList<String>,
    val receiptNames : MutableList<String>,
) {
    constructor():this(ArrayList(),ArrayList())
}