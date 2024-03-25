package app.ocr_backend.repository

import app.ocr_backend.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserDBRepository:JpaRepository<User,Long> {
    //TODO("UserDBRepository")
}