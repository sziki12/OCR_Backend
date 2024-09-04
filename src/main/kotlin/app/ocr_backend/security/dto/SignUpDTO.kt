package app.ocr_backend.security.dto

import app.ocr_backend.user.User

data class SignUpDTO(
    val userName:String,
    val password:String,
    val salt:String
)
{
    fun toUser(): User
    {
        return User(
            userName,
            "",
            salt,
        )
    }
}
