package app.ocr_backend.user
import app.ocr_backend.security.dto.CredentialsDto
import app.ocr_backend.security.dto.SignUpDto
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class UserService(
    val repository: UserRepository,
    val passwordEncoder: PasswordEncoder,
) {

    fun registerUser(signUpDto: SignUpDto): User
    {
        val existingUSer = repository.findByEmail(signUpDto.email)
        if(existingUSer.isPresent)
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

    fun updateUser()
    {

    }

    fun deleteUser()
    {

    }

    fun findByEmail(email:String): Optional<User> {
        return repository.findByEmail(email)
    }
}