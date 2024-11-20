package app.ocr_backend.ai.ocr.ocr_entity

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface OcrEntityRepository:JpaRepository<OcrEntity,Long> {

    fun getOcrEntityById(entityId:Long): Optional<OcrEntity>
}