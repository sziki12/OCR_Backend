package app.ocr_backend.email


import app.ocr_backend.household.HouseholdService
import app.ocr_backend.security.auth.AuthService
import jakarta.mail.*
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeBodyPart
import jakarta.mail.internet.MimeMessage
import jakarta.mail.internet.MimeMultipart
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*


@Service
class EmailService {

    @Value("\${gmail.email}")
    lateinit var email: String

    @Value("\${gmail.password}")
    lateinit var password: String

    fun sendEmail(targetEmail:String, subject:String, content:String) {
        val prop = Properties()
        prop["mail.smtp.host"] = "smtp.gmail.com"
        prop["mail.smtp.port"] = "465"
        prop["mail.smtp.auth"] = "true"
        prop["mail.smtp.socketFactory.port"] = "465"
        prop["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"

        val session: Session = Session.getInstance(prop, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication = PasswordAuthentication(email, password)
        })

        val message: Message = MimeMessage(session)
        message.setFrom(InternetAddress(email))
        message.setRecipients(
            Message.RecipientType.TO, InternetAddress.parse(targetEmail)
        )
        message.subject = subject

        val mimeBodyPart = MimeBodyPart()
        mimeBodyPart.setContent(content, "text/html; charset=utf-8")

        val multipart: Multipart = MimeMultipart()
        multipart.addBodyPart(mimeBodyPart)

        message.setContent(multipart)

        Transport.send(message)

    }


}