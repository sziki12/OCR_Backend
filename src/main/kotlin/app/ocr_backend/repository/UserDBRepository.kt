package app.ocr_backend.repository

import app.ocr_backend.model.Item
import app.ocr_backend.model.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserDBRepository:JpaRepository<User,Long> {


    fun findByLogin(login: String): Optional<User>
}