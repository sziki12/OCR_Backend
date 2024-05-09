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

    fun getLlamaStartDir():Path
    {
        val path = getPythonDir().pathString+"${s}llama"
        val file = File(path)
        return file.toPath()
    }

    fun getOcrStartDir():Path
    {
        val path = getPythonDir().pathString+"${s}ocr"
        val file = File(path)
        return file.toPath()
    }

    fun getLlamaInputDir():Path
    {
        val path = getResourceDir().pathString+"${s}llama${s}input_text"
        val file = File(path)
        return file.toPath()
    }

    fun getLlamaOutputDir():Path
    {
        val path = getResourceDir().pathString+"${s}llama${s}output_text"
        val file = File(path)
        return file.toPath()
    }

    fun getPythonDir():Path
    {
        val path = getMainDir().pathString+"${s}python"
        val file = File(path)
        return file.toPath()
    }

    fun getModelDir():Path
    {
        //val path = "D:\\Llama\\Files\\Models\\llama2_7b\\llama-2-7b-chat.Q6_K.gguf"
        val path = "C:\\Users\\Szikszai Levente\\Llama\\Files\\Models\\llama2_7b\\llama-2-7b-chat.Q6_K.gguf"
        return File(path).toPath()
    }
}