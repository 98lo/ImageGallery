package com.example.imagegallery.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.util.UUID

object FileUtils {
    fun copyImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val dir = File(context.filesDir, "images")
            if (!dir.exists()) dir.mkdirs()
            val fileName = "img_${UUID.randomUUID()}.jpg"
            val outFile = File(dir, fileName)
            inputStream.use { input ->
                outFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            outFile.absolutePath
        } catch (e: Exception) {
            null
        }
    }
}
