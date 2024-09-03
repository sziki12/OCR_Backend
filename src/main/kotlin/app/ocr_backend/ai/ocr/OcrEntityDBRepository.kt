package app.ocr_backend.ai.ocr

import app.ocr_backend.ai.ocr.OcrEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface OcrEntityDBRepository:JpaRepository<OcrEntity,Long> {

    fun getOcrEntityById(entityId:Long): Optional<OcrEntity>
}