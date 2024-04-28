package app.ocr_backend.service

import app.ocr_backend.model.OcrEntity
import app.ocr_backend.repository.OcrEntityDBRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class OcrEntityService(private val repository:OcrEntityDBRepository) {
    fun saveOcrEntity(entity:OcrEntity): OcrEntity {
        return repository.save(entity)
    }

    fun getOcrEntity(entityId:Long): Optional<OcrEntity> {
        return repository.getOcrEntityById(entityId)
    }

    fun deleteOcrEntity(entityId:Long) {
        repository.deleteById(entityId)
    }
}