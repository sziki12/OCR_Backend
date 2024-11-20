package app.ocr_backend.receipt_image

import app.ocr_backend.util.PathHandler
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.*
import kotlin.io.path.pathString


@RestController
@RequestMapping("/api/household/{householdId}/image")
@CrossOrigin
class ImageController(
    val imageService: ImageService
) {

    @GetMapping(value = ["/{receiptId}/{imageId}"], produces = [MediaType.IMAGE_JPEG_VALUE])
    @ResponseBody
    @Throws(
        IOException::class
    )
    fun getImage(@PathVariable receiptId:Long, @PathVariable imageId:Long, @PathVariable householdId: UUID): ByteArrayResource? {
        val imageName = imageService.getImageName(receiptId, imageId)//TODO householdID
        if (imageName.isPresent)
        {
            val imagePath = PathHandler.getImageDir().pathString+File.separator+imageName.get()
            val image = File(imagePath)
            return ByteArrayResource(Files.readAllBytes(image.toPath()))
        }
        return null
    }

    /*@GetMapping(value = ["/{receiptId}"])
    fun getAllImageByReceiptId(@PathVariable receiptId:Long): List<ByteArrayResource> {
        val images = imageService.getImages(receiptId)
        val list = ArrayList<ByteArrayResource>()
        for(image  in images)
        {
            val imagePath = PathHandler.getImageDir().pathString+File.separator+image.name
            val imageFile = File(imagePath)
            list.add(ByteArrayResource(Files.readAllBytes(imageFile.toPath())))
        }
        return list
    }*/
}