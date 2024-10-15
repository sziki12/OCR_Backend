package app.ocr_backend.exceptions

import java.util.*

class ElementNotExists(override val message:String): Exception() {

    companion object{
        fun fromReceipt(householdId: UUID, receiptId:Long): ElementNotExists {
            return ElementNotExists("Receipt with id: $receiptId not found in household: $householdId")
        }

        fun fromUser(userId:Long): ElementNotExists {
            return ElementNotExists("User with id: $userId not found")
        }

        fun fromUser(email:String): ElementNotExists {
            return ElementNotExists("User with email: $email not found")
        }

        fun fromHouseholdInvitation(invitationId:UUID): ElementNotExists {
            return ElementNotExists("HouseholdInvitation with id: $invitationId not found")
        }
    }
}