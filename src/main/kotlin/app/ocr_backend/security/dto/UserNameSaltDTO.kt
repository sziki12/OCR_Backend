package app.ocr_backend.security.dto

data class UserNameSaltDTO(
    var userName:String,
    var salt:String) {
}