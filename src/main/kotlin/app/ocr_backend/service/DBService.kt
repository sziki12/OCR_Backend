package app.ocr_backend.service

import app.ocr_backend.dto.OcrResponse
import app.ocr_backend.model.*
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.*
import kotlin.collections.ArrayList

@Service
class DBService(
    private val receiptService: ReceiptService,
    private val itemService: ItemService,
    private val imageService: ImageService,
    private val userService: UserService,
    private val placeService: PlaceService,
    private val ocrEntityService: OcrEntityService,
) {

    //ITEM
    fun createNewItem(receiptId: Long): Optional<Item> {
        val receipt = receiptService.getReceipt(receiptId)
        return if(receipt.isPresent)
             Optional.of(itemService.createNewItem(receipt.get()))
        else Optional.empty()
    }

    fun saveItem(receiptId: Long, item: Item)
    {
        val receipt = receiptService.getReceipt(receiptId)
        if(receipt.isPresent)
            itemService.saveItem(receipt.get(),item)
    }
    fun getItem(itemId:Long): Optional<Item> {
        return itemService.getItem(itemId)
    }

    fun updateItem(item: Item)
    {
        itemService.updateItem(item)
    }

    fun deleteItem(itemId:Long)
    {
        itemService.deleteItem(itemId)
    }

    //RECEIPT

    fun saveReceipt(receipt: Receipt): Optional<Receipt> {
        val optUser = getCurrentUser()
        if(optUser.isPresent)
        {
            receipt.user = optUser.get()
            val savedReceipt = receiptService.saveReceipt(receipt)
            return Optional.of(savedReceipt)
        }
        return Optional.empty()
    }
    fun getReceipt(receiptId:Long): Optional<Receipt> {
       val optUser = getCurrentUser()
        return if(optUser.isPresent) {
            val user = optUser.get()
            user.receipts.find { it.id == receiptId }?.let { Optional.of(it) } ?: Optional.empty<Receipt>()
        } else
            Optional.empty<Receipt>()
    }

    fun updateReceipt(receipt: Receipt)
    {
        for(item in receipt.items)
            itemService.updateItem(item)
        receiptService.updateReceipt(receipt)
    }

    @Transactional
    fun deleteReceipt(receiptId:Long)
    {
        val receipt = receiptService.getReceipt(receiptId)
        if(receipt.isPresent)
        {
            imageService.deleteAllByReceipt(receipt.get())
            itemService.deleteAllByReceipt(receipt.get())
            receipt.get().ocrEntity?.let {
                ocrEntityService.deleteOcrEntity(it.id)
            }
            receiptService.deleteReceipt(receiptId)
        }
    }

    fun getAllReceipt():List<Receipt>
    {
        val optUser = getCurrentUser()
        return if(optUser.isPresent)
            optUser.get().receipts
        else
            listOf()
    }

    //IMAGE
    fun getImages(receiptId:Long): List<ReceiptImage> {
        return imageService.getImages(receiptId)
    }

    fun saveImage(receiptId: Long,imageName:String)
    {
        val receipt = receiptService.getReceipt(receiptId)
        if(receipt.isPresent)
            imageService.saveImage(receipt.get(),imageName)
    }

    fun deleteImage(imageId:Long)
    {
        return imageService.deleteImage(imageId)
    }

    fun generateImageName(receipt: Receipt,image: MultipartFile):String
    {
        return imageService.generateImageName(receipt,image)
    }

    private fun getCurrentUser(): Optional<User> {
        val actualUserName = SecurityContextHolder.getContext().authentication.principal.toString()
        return userService.findByUserName(actualUserName)
    }

    //PLACE

    fun savePlace(place: Place): Optional<Place> {
        val optUser = getCurrentUser()
        if(optUser.isPresent)
        {
            place.user = optUser.get()
            if(place.user.isAdmin)
                place.isValidated = true
            return Optional.of(placeService.savePlace(place))
        }
        return Optional.empty()
    }

    fun assignPlaceToReceipt(receiptId: Long,placeId: Long)
    {
        val optReceipt = receiptService.getReceipt(receiptId)
        if(optReceipt.isPresent)
        {
            val receipt = optReceipt.get()
            val optPlace = placeService.getPlace(placeId)
            if(optPlace.isPresent)
            {
                val place = optPlace.get()
                receipt.place = place
                receiptService.updateReceipt(receipt)
            }
        }
    }

    fun removePlaceFromReceipt(receiptId: Long)
    {
        val optReceipt = receiptService.getReceipt(receiptId)
        if(optReceipt.isPresent)
        {
            val receipt = optReceipt.get()

            receipt.place = null
            receiptService.updateReceipt(receipt)
        }
    }

    fun validatePlace(placeId: Long)
    {
        val optUser = getCurrentUser()
        if(optUser.isPresent)
        {
            if(optUser.get().isAdmin)
            {
                placeService.validatePlace(placeId)
            }
        }
    }

    fun deletePlace(placeId: Long)
    {
        placeService.deletePlace(placeId)
    }

    fun getPlaces(): List<Place> {
        return placeService.getPlaces()
    }

    fun mergePlaces(holderId:Long,partId:Long)
    {
        val holder = placeService.getPlace(holderId)
        val part = placeService.getPlace(partId)
        if(holder.isPresent && part.isPresent)
        {
            for(receipt in part.get().receipts)
            {
                receipt.place = holder.get()
                receiptService.saveReceipt(receipt)
            }
            placeService.deletePlace(partId)
        }
    }

    //OcrEntity

    fun saveOcrEntity(receiptId:Long,entity:OcrEntity)
    {
        val optReceipt = receiptService.getReceipt(receiptId)
        if(optReceipt.isPresent)
        {
            entity.receipt = optReceipt.get()
            ocrEntityService.saveOcrEntity(entity)
        }
    }

    fun createFromOcrResponse(ocrResponse: OcrResponse)
    {
        val optReceipt = receiptService.getReceipt(ocrResponse.newReceiptId)
        if(optReceipt.isPresent)
        {
            val receipt = optReceipt.get()
            receipt.dateOfPurchase = ocrResponse.date
            val ocrEntity = OcrEntity.fromOcrResponse(ocrResponse).also {
                it.receipt = receipt
                it.date = receipt.dateOfPurchase
            }
            receiptService.saveReceipt(receipt)
            ocrEntityService.saveOcrEntity(ocrEntity)
        }
    }

    fun getOcrEntity(entityId:Long): Optional<OcrEntity> {
        return ocrEntityService.getOcrEntity(entityId)
    }

    fun getOcrResponse(receiptId:Long): Optional<OcrResponse> {
        val optReceipt = receiptService.getReceipt(receiptId)
        if(optReceipt.isPresent)
        {
            optReceipt.get().ocrEntity?.let {
                return Optional.of(it.toOcrResponse())
            }
        }
        return Optional.empty()
    }
}