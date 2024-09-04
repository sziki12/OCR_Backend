package app.ocr_backend.filter

data class FilterOptionsDto(
    val placeNames : MutableList<String>,
    val receiptNames : MutableList<String>,
) {
    constructor():this(ArrayList(),ArrayList())
}