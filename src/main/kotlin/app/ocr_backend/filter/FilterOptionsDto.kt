package app.ocr_backend.filter

data class FilterOptionsDto(
    val placeNames : MutableSet<String>,
    val receiptNames : MutableSet<String>,
) {
    constructor():this(mutableSetOf(),mutableSetOf())
}