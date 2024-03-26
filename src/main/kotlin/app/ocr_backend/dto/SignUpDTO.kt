package app.ocr_backend.dto

import app.ocr_backend.model.User

data class SignUpDTO(
    val userName:String,
    val password:String
)
{
    fun toUser():User
    {
        return User(
            userName,
            ""
        )
    }
}
