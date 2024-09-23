package app.ocr_backend.receipt

import app.ocr_backend.ai.ocr.frontend_dto.OcrResponse
import app.ocr_backend.ai.ocr.ocr_entity.OcrEntity
import app.ocr_backend.ai.ocr.ocr_entity.OcrEntityRepository
import app.ocr_backend.household.HouseholdService
import app.ocr_backend.item.Item
import app.ocr_backend.item.ItemRepository
import app.ocr_backend.place.PlaceRepository
import app.ocr_backend.receipt_image.ImageRepository
import app.ocr_backend.security.auth.AuthService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.*

@Service
class ReceiptService(
    val receiptRepository: ReceiptRepository,
    val itemRepository: ItemRepository,
    val imageRepository: ImageRepository,
    val ocrEntityRepository: OcrEntityRepository,
    val placeRepository: PlaceRepository,
    val householdService: HouseholdService,
    val authService: AuthService,
) {
    fun saveReceipt(householdId: UUID, receipt: Receipt): Optional<Receipt> {
        val optHUser = authService.getCurrentHouseholdUser(householdId)
        if (optHUser.isPresent.not())
            return Optional.empty<Receipt>()
        //TODO Saving exisiting item id
        for (item in receipt.items) {
            itemRepository.save(item)
        }
        val savedReceipt = receiptRepository.save(receipt.also { it.household = optHUser.get().household })
        for (item in receipt.items) {
            itemRepository.save(item.also { it.receipt = savedReceipt })
        }
        return Optional.of(savedReceipt)
    }

    fun getReceipt(householdId: UUID, receiptId: Long): Optional<Receipt> {
        val optHUser = authService.getCurrentHouseholdUser(householdId)
        if (optHUser.isPresent.not())
            return Optional.empty<Receipt>()
        val household = optHUser.get().household
        return household.receipts.find { it.id == receiptId }?.let { Optional.of(it) } ?: Optional.empty<Receipt>()
    }

    fun updateReceipt(householdId: UUID, receipt: Receipt): Optional<Receipt> {
        val optReceipt = this.getReceipt(householdId, receipt.id)
        for (item in receipt.items) {
            itemRepository.save(item)
        }

        if (optReceipt.isPresent) {
            val originalReceipt = optReceipt.get()
            val itemsToRemove = ArrayList<Item>()
            itemsToRemove.addAll(originalReceipt.items)
            itemsToRemove.removeAll(receipt.items.toSet())
            for (item in itemsToRemove) {
                itemRepository.deleteById(item.id)
            }
            return Optional.of(receiptRepository.save(receipt))
        }
        return Optional.empty()
    }

    @Transactional
    fun deleteReceipt(householdId: UUID, receiptId: Long) {
        val receipt = this.getReceipt(householdId, receiptId)
        if (receipt.isPresent) {
            imageRepository.deleteAllByReceipt(receipt.get())
            receipt.get().ocrEntity?.let {
                ocrEntityRepository.deleteById(it.id)
            }
            this.deleteReceipt(householdId, receiptId)
        }
    }
    /*fun getAllReceipt():List<Receipt>
    {
        val optUser = authService.getCurrentUser()
        return if(optUser.isPresent)
            optUser.get().receipts
        else
            listOf()
    }*/

    fun getReceiptsByHousehold(householdId: UUID): List<Receipt> {
        val optHUser = authService.getCurrentHouseholdUser(householdId)
        if (optHUser.isPresent.not())
            return listOf()
        val household = optHUser.get().household
        return receiptRepository.getByHousehold(household)
    }

    fun assignPlaceToReceipt(householdId: UUID, receiptId: Long, placeId: Long) {
        val optReceipt = this.getReceipt(householdId, receiptId)
        if (optReceipt.isPresent) {
            val receipt = optReceipt.get()
            val optPlace = placeRepository.findById(placeId)
            if (optPlace.isPresent) {
                val place = optPlace.get()
                receipt.place = place
                this.updateReceipt(householdId, receipt)
            }
        }
    }

    fun removePlaceFromReceipt(householdId: UUID, receiptId: Long) {
        val optReceipt = this.getReceipt(householdId, receiptId)
        if (optReceipt.isPresent) {
            val receipt = optReceipt.get()

            receipt.place = null
            this.updateReceipt(householdId, receipt)
        }
    }

    fun saveAndAssignOcrEntity(householdId: UUID, receiptId: Long, entity: OcrEntity) {
        val optReceipt = this.getReceipt(householdId, receiptId)
        if (optReceipt.isPresent) {
            entity.receipt = optReceipt.get()
            ocrEntityRepository.save(entity)
        }
    }

    fun getOcrResponse(householdId: UUID, receiptId: Long): Optional<OcrResponse> {
        val optReceipt = this.getReceipt(householdId, receiptId)
        if (optReceipt.isPresent) {
            optReceipt.get().ocrEntity?.let {
                return Optional.of(it.toOcrResponse())
            }
        }
        return Optional.empty()
    }
}

