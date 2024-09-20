package app.ocr_backend.user

import app.ocr_backend.user.User

data class UserDTO (
    val name:String,
    val email:String,
    var token:String,
    var salt:String,
)
{
    fun toUser(): User {
        return User(
            name,
            email,
            token,
            salt
        )
    }

    constructor(user: User):this(user.name, user.email,"",user.salt)
}