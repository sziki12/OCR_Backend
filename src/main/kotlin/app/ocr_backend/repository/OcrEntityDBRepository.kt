package app.ocr_backend.repository

import app.ocr_backend.model.Item
import app.ocr_backend.model.OcrEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface OcrEntityDBRepository:JpaRepository<OcrEntity,Long> {

    fun getOcrEntityById(entityId:Long): Optional<OcrEntity>
}