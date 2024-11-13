package app.ocr_backend.user.dto

import app.ocr_backend.security.dto.TokenDto
import app.ocr_backend.user.User

data class UserDTO (
    val name:String,
    val email:String,
    var tokens:TokenDto,
    var salt:String,
)
{
    fun toUser(): User {
        return User(
            name,
            email,
            "",
            salt
        )
    }

    constructor(user: User):this(user.name, user.email,TokenDto(),user.salt)
}