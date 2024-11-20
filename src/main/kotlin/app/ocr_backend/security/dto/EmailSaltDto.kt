package app.ocr_backend.security.dto

data class EmailSaltDto(
    var email:String,
    var salt:String) {
}