package app.ocr_backend.security.dto

data class TokenDto(
    var authToken:String,
    var refreshToken:String,
){
    constructor():this("","")
}
