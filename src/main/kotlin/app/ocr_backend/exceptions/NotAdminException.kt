package app.ocr_backend.exceptions

import app.ocr_backend.user.User
import java.util.UUID

class NotAdminException(override val message: String):Exception() {

    companion object{
        fun fromUser(user: User, householdId:UUID): NotAdminException {
            return NotAdminException("User with email: ${user.email} is not admin in household with id: $householdId")
        }
    }
}