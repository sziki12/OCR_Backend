package app.ocr_backend.exceptions

class EmailNotSent(override val message: String?):Exception() {

    companion object{
        fun fromInvitation(): EmailNotSent {
            return EmailNotSent("Couldn't send email due to trying to invite the current user or " +
                    "somebody how is already part of the household")
        }
    }
}