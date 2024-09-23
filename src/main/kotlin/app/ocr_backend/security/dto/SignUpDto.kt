package app.ocr_backend.security.dto

import app.ocr_backend.user.User

data class SignUpDto(
    val name:String,
    val email:String,
    val password:String,
    val salt:String
)
{
    fun toUser(): User
    {
        return User(
            name,
            email,
            "",
            salt,
        )
    }
}
