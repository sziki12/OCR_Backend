package app.ocr_backend.controller

import app.ocr_backend.service.ImageService
import app.ocr_backend.utils.PathHandler
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.pathString


@RestController
@RequestMapping("/api/image")
@CrossOrigin
class ImageController(
    val imageService:ImageService
) {

    @GetMapping(value = ["/{receiptId}/{imageId}"], produces = [MediaType.IMAGE_JPEG_VALUE])
    @ResponseBody
    @Throws(
        IOException::class
    )
    fun getImage(@PathVariable receiptId:Long,@PathVariable imageId:Long): ByteArrayResource? {
        val imageName = imageService.getImageName(receiptId, imageId)
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