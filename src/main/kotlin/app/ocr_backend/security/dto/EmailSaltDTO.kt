package app.ocr_backend.security.dto

data class EmailSaltDTO(
    var email:String,
    var salt:String) {
}