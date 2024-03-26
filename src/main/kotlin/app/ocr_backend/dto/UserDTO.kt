package app.ocr_backend.dto

import app.ocr_backend.model.User

data class UserDTO (
    val name:String,
    val login:String,
    var token:String,
)
{
    fun toUser(): User {
        return User(
            name,
            login,
            token
        )
    }

    constructor(user:User):this(user.name,user.login,"")
}