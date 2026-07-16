package com.example.imagegallery.utils

import android.graphics.BitmapFactory
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object OcrUtils {
    suspend fun recognizeText(filePath: String): String = suspendCancellableCoroutine { cont ->
        try {
            val bitmap = BitmapFactory.decodeFile(filePath)
            if (bitmap == null) {
                cont.resume("")
                return@suspendCancellableCoroutine
            }
            val image = InputImage.fromBitmap(bitmap, 0)
            val recognizer = TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())
            recognizer.process(image)
                .addOnSuccessListener { visionText -> cont.resume(visionText.text) }
                .addOnFailureListener { cont.resume("") }
        } catch (e: Exception) {
            cont.resume("")
        }
    }
}
