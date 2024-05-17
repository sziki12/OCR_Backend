package app.ocr_backend.dto

import app.ocr_backend.model.User

data class UserDTO (
    val userName:String,
    var token:String,
    var salt:String,
)
{
    fun toUser(): User {
        return User(
            userName,
            token,
            salt
        )
    }

    constructor(user:User):this(user.userName,"",user.salt)
}