package app.ocr_backend.service

import app.ocr_backend.model.Receipt
import app.ocr_backend.model.ReceiptImage
import app.ocr_backend.repository.ImageDBRepository
import app.ocr_backend.utils.PathHandler
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.util.*
import kotlin.io.path.pathString

@Service
class ImageService(private val imageRepository: ImageDBRepository) {

    fun getImages(receiptId:Long): List<ReceiptImage> {
        return imageRepository.getByReceiptId(receiptId)
    }

    fun getImageName(receiptId:Long,imageID:Long): Optional<String> {
        val imageName =  imageRepository.getByReceiptId(receiptId).singleOrNull { it.id == imageID }
        imageName?.let {
            return Optional.of(it.name)
        }
        return Optional.empty()
    }

    fun saveImage(receipt: Receipt,imageName:String)
    {
        val image = ReceiptImage(imageName)
        image.receipt = receipt
        receipt.images.add(image)
        imageRepository.save(image)
    }

    fun deleteImage(imageId:Long)
    {
        //TEST File deletion
        val optImage = imageRepository.getReceiptImageById(imageId)
        if(optImage.isPresent)
        {
            val image = File(PathHandler.getImageDir().pathString+File.separator+optImage.get().name)
            val input = File(PathHandler.getLlamaInputDir().pathString+File.separator+optImage.get().name.replace(".jpg",".txt"))
            val output = File(PathHandler.getLlamaOutputDir().pathString+File.separator+optImage.get().name.replace(".jpg",".txt"))
            try {
                image.delete()
                input.delete()
                output.delete()
            }
            catch (e:Exception)
            {
                System.err.println("Failed to delete files")
                e.printStackTrace()
            }
        }

        return imageRepository.deleteById(imageId)
    }

    fun generateImageName(receipt: Receipt,image: MultipartFile):String
    {
        val altName = "file.jpg"
        //TODO ADD USER ID TO FRONT IF NEEDED
        val fileName = (image.originalFilename?: altName).split('.')
        return ("${fileName[0]}${receipt.id}"+"${receipt.images.size}.${fileName[1]}")
    }

    fun deleteAllByReceipt(receipt: Receipt)
    {
        imageRepository.deleteAllByReceipt(receipt)
    }
}