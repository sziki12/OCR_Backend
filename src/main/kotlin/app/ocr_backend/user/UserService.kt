package app.ocr_backend.user
import app.ocr_backend.email.EmailService
import app.ocr_backend.exceptions.ElementNotExists
import app.ocr_backend.security.dto.CredentialsDto
import app.ocr_backend.security.dto.SignUpDto
import app.ocr_backend.user.registration_confirmation.RegistrationConfirmation
import app.ocr_backend.user.registration_confirmation.RegistrationConfirmationRepository
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class UserService(
    val repository: UserRepository,
    val registrationConfirmationRepository:RegistrationConfirmationRepository,
    val passwordEncoder: PasswordEncoder,
    val emailService: EmailService,
) {

    @Value("\${server.url}")
    lateinit var serverUrl:String

    fun registerUser(signUpDto: SignUpDto): User
    {
        val existingUser = repository.findByEmail(signUpDto.email)
        if(existingUser.isPresent)
        {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST,"User Already Exists")
        }

        val user = signUpDto.toUser()

        user.password = passwordEncoder.encode(signUpDto.password)
        repository.save(user)
        return user
    }

    fun loginUser(credentials: CredentialsDto): User
    {
        val user = repository.findByEmail(credentials.email).orElseThrow{
            ResponseStatusException(HttpStatus.NOT_FOUND,"User Not Exists")
        }

        if(passwordEncoder.matches(credentials.password,user.password))
        {
            return user
        }

        throw ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid Password")

    }

    @Transactional
    fun confirmEmail(confirmationId: UUID){

        val confirmation = registrationConfirmationRepository.findById(confirmationId)
            .orElseThrow{ ElementNotExists.fromRegistrationConfirmation(confirmationId) }
        val user = this.findById(confirmation.registeredUserId)
            .orElseThrow { ElementNotExists.fromUser(confirmation.registeredUserId) }
        user.isEmailConfirmed = true
        repository.save(user)
        registrationConfirmationRepository.deleteAllByRegisteredUserId(user.id)
    }

    fun sendEmailConfirmation(email:String){
        val user = repository.findByEmail(email).orElseThrow{ElementNotExists.fromUser(email)}
        var confirmation = RegistrationConfirmation(user.id)
        confirmation = registrationConfirmationRepository.save(confirmation)
        val confirmUrl = "$serverUrl/api/confirmation/${confirmation.id}"
        val content = "To finis your registration please confirm your email address using this link:\n${confirmUrl}"
        emailService.sendEmail(email,"Email Confirmation",content)
    }

    fun findByEmail(email:String): Optional<User> {
        return repository.findByEmail(email)
    }

    fun findById(userId: Long): Optional<User> {
        return repository.findById(userId)
    }
}