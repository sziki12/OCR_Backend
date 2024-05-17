package app.ocr_backend.dto

data class UserNameSaltDTO(
    var userName:String,
    var salt:String) {
}