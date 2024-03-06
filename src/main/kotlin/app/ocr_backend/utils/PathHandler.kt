package app.ocr_backend.utils

import java.io.File
import java.nio.file.Path
import kotlin.io.path.pathString

object PathHandler {
    private val s = File.separator
    fun getMainDir():Path
    {

        val path = System.getProperty("user.dir")+"${s}src${s}main"
        val file = File(path)
        return file.toPath()
    }

    fun getResourceDir():Path
    {
        val path = getMainDir().pathString+"${s}resources"
        val file = File(path)
        return file.toPath()
    }

    fun getImageDir():Path
    {
        val path = getResourceDir().pathString+"${s}image"
        val file = File(path)
        return file.toPath()
    }

    fun getPythonDir():Path
    {
        val path = getMainDir().pathString+"${s}python"
        val file = File(path)
        return file.toPath()
    }
}