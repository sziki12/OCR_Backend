package app.ocr_backend.ai.ocr.ocr_entity

import org.springframework.stereotype.Service
import java.util.*

@Service
class OcrEntityService(private val repository: OcrEntityRepository) {
    fun saveOcrEntity(entity: OcrEntity): OcrEntity {
        return repository.save(entity)
    }

    fun getOcrEntity(entityId:Long): Optional<OcrEntity> {
        return repository.getOcrEntityById(entityId)
    }

    fun deleteOcrEntity(entityId:Long) {
        repository.deleteById(entityId)
    }
}