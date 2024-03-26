package app.ocr_backend.dto

import app.ocr_backend.model.User

data class SignUpDTO(
    val name:String,
    val login:String,
    val password:String
)
{
    fun toUser():User
    {
        return User(
            name,
            login,
            ""
        )
    }
}
