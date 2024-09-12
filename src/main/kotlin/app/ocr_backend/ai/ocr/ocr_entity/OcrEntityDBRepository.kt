package app.ocr_backend.ai.ocr.ocr_entity

import app.ocr_backend.ai.ocr.ocr_entity.OcrEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface OcrEntityDBRepository:JpaRepository<OcrEntity,Long> {

    fun getOcrEntityById(entityId:Long): Optional<OcrEntity>
}