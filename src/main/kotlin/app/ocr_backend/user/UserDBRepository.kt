package app.ocr_backend.user

import app.ocr_backend.user.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserDBRepository:JpaRepository<User,Long> {


    fun findByUserName(userName: String): Optional<User>
}