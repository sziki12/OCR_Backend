package app.ocr_backend.security.dto

data class RefreshTokenDto(
    var userId:Long,
    var refreshToken:String
)