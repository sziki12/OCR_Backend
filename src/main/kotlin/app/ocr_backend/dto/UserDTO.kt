package app.ocr_backend.dto

import app.ocr_backend.model.User

data class UserDTO (
    val userName:String,
    var token:String,
)
{
    fun toUser(): User {
        return User(
            userName,
            token
        )
    }

    constructor(user:User):this(user.userName,"")
}