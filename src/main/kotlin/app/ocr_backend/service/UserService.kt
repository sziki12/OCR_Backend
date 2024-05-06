package app.ocr_backend.service
import app.ocr_backend.config.security.UserAuthProvider
import app.ocr_backend.dto.CredentialsDTO
import app.ocr_backend.dto.SignUpDTO
import app.ocr_backend.model.User
import app.ocr_backend.repository.UserDBRepository
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class UserService(
    val repository: UserDBRepository,
    val passwordEncoder: PasswordEncoder,
) {

    //val passwordEncoder = BCryptPasswordEncoder()

    fun registerUser(signUpDto: SignUpDTO):User
    {
        val existingUSer = repository.findByUserName(signUpDto.userName)
        if(existingUSer.isPresent)
        {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST,"User Already Exists")
        }

        val user = signUpDto.toUser()

        user.password = passwordEncoder.encode(signUpDto.password)
        repository.save(user)
        return user
    }

    fun loginUser(credentials:CredentialsDTO):User
    {
        val user = repository.findByUserName(credentials.userName).orElseThrow{
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

    fun findByUserName(userName:String): Optional<User> {
        return repository.findByUserName(userName)
    }
}