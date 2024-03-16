package app.ocr_backend.service

import app.ocr_backend.model.Receipt
import app.ocr_backend.model.ReceiptImage
import app.ocr_backend.repository.ImageDBRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File

@Service
class ImageService(private val repository: ImageDBRepository) {

    fun getImages(receiptId:Long): List<ReceiptImage> {
        return repository.getByReceiptId(receiptId)
    }

    fun saveImage(receipt: Receipt,imageName:String)
    {
        val image = ReceiptImage(imageName)
        image.receipt = receipt
        receipt.images.add(image)
        repository.save(image)
    }

    fun deleteImage(imageId:Long)
    {
        return repository.deleteById(imageId)
    }

    fun generateImageName(receipt: Receipt,image: MultipartFile):String
    {
        val altName = "file.jpg"
        //TODO ADD USER ID TO FRONT
        val fileName = (image.originalFilename?: altName).split('.')
        return ("${fileName[0]}${receipt.id}"+"${receipt.images.size}.${fileName[1]}")
    }
}