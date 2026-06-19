package com.noemi_balazs.animator.camera

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat.getMainExecutor
import com.noemi_balazs.animator.utils.MIME_TYPE
import com.noemi_balazs.animator.utils.RELATIVE_PATH

object CameraUtil {

    fun saveToMediaStore(
        bitmap: Bitmap,
        contentResolver: ContentResolver,
        onResult: (Uri?) -> Unit
    ) {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "animator_${System.currentTimeMillis()}.jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, RELATIVE_PATH)
            put(MediaStore.Images.Media.MIME_TYPE, MIME_TYPE)
            put(MediaStore.Images.Media.WIDTH, bitmap.width)
            put(MediaStore.Images.Media.HEIGHT, bitmap.height)
        }

        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        runCatching {
            uri?.let { uri ->
                contentResolver.openOutputStream(uri)?.use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream).also {
                        onResult(uri)
                    }
                }
            }
        }.onFailure { error ->
            onResult(null)
            println("Animator - error while saving bitmap to gallery: ${error.message}")
        }
    }

    fun takePhoto(
        controller: LifecycleCameraController,
        context: Context,
        onResult: (Bitmap) -> Unit
    ) {
        controller.takePicture(
            getMainExecutor(context),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                    super.onCaptureSuccess(imageProxy)

                    val bitmap = imageProxy.toBitmap()
                    val rotationDegrees = imageProxy.imageInfo.rotationDegrees

                    val matrix = Matrix().apply {
                        postRotate(rotationDegrees.toFloat())
                    }

                    val rotatedBitmap = Bitmap.createBitmap(
                        bitmap,
                        0,
                        0,
                        bitmap.width,
                        bitmap.height,
                        matrix,
                        true
                    )
                    onResult(rotatedBitmap).also {
                        imageProxy.close()
                    }
                }
            }
        )
    }
}