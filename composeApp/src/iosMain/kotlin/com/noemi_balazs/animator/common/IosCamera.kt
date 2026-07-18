package com.noemi_balazs.animator.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import platform.UIKit.UIImage
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.noemi_balazs.animator.resources.Res
import com.noemi_balazs.animator.resources.camera
import com.noemi_balazs.animator.resources.delete
import com.noemi_balazs.animator.resources.save
import com.noemi_balazs.animator.ui.component.AnimatorImageButton
import com.noemi_balazs.animator.ui.component.AnimatorProgressBar
import com.noemi_balazs.animator.util.toImageBitmap
import org.koin.compose.koinInject
import platform.Foundation.NSURL
import platform.Foundation.*
import platform.Photos.PHAssetChangeRequest
import platform.Photos.PHPhotoLibrary
import platform.UIKit.UIImageJPEGRepresentation

class IosCamera(private val cameraActionHandler: CameraActionHandler) : Camera {

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    override fun CameraView(onSuccess: (String) -> Unit, onError: () -> Unit) {

        var capturedImage by remember { mutableStateOf<UIImage?>(null) }
        var cameraIsReady by remember { mutableStateOf(false) }

        BackHandler { cameraActionHandler.stop() }

        LaunchedEffect(Unit) {
            cameraActionHandler.start()
        }

        DisposableEffect(cameraActionHandler) {
            cameraActionHandler.onPhotoCaptured { uiImage ->
                capturedImage = uiImage
            }

            cameraActionHandler.cameraIsReady { isReady ->
                cameraIsReady = isReady
            }

            onDispose {
                cameraActionHandler.stop()
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CameraPreview(cameraActionHandler) {
                TakePhotoActionRow(
                    {
                        cameraActionHandler.takePhoto()
                    }
                )
            }

            capturedImage?.let { image ->
                CameraResultContent(
                    image = image,
                    onSave = {
                        val url = saveImageToLibrary(image)
                        url?.let { path ->
                            onSuccess(path.toString())
                        } ?: onError()
                    },
                    onDelete = {
                        capturedImage = null
                    }
                )
            }

            if (!cameraIsReady) AnimatorProgressBar()
        }
    }

    @Composable
    private fun TakePhotoActionRow(takePhoto: () -> Unit) {
        Box(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                AnimatorImageButton(
                    onClick = takePhoto,
                    colorTint = MaterialTheme.colorScheme.primary,
                    backgroundColor = Color.White,
                    resource = Res.drawable.camera
                )
            }
        }
    }

    @Composable
    private fun CameraResultContent(
        image: UIImage,
        onSave: () -> Unit,
        onDelete: () -> Unit,
        modifier: Modifier = Modifier
    ) {

        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {

            Image(
                bitmap = image.toImageBitmap(),
                modifier = Modifier.fillMaxSize(),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )

            Row(
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {

                AnimatorImageButton(
                    resource = Res.drawable.delete,
                    backgroundColor = MaterialTheme.colorScheme.onPrimary,
                    colorTint = MaterialTheme.colorScheme.primary,
                    onClick = onDelete
                )

                Spacer(Modifier.padding(20.dp))

                AnimatorImageButton(
                    resource = Res.drawable.save,
                    colorTint = MaterialTheme.colorScheme.primary,
                    backgroundColor = MaterialTheme.colorScheme.onPrimary,
                    onClick = onSave
                )
            }
        }
    }

    private fun saveImageToLibrary(
        image: UIImage,
        fileName: String = "${NSUUID().UUIDString}.jpg"
    ): NSURL? {
        PHPhotoLibrary.sharedPhotoLibrary().performChanges(
            changeBlock = {
                PHAssetChangeRequest.creationRequestForAssetFromImage(image)
            },
            completionHandler = { _, _ -> }
        )

        val data = UIImageJPEGRepresentation(image, 0.95) ?: return null

        val documents = NSFileManager.defaultManager
            .URLsForDirectory(NSDocumentDirectory, NSUserDomainMask)
            .firstOrNull() as? NSURL ?: return null

        val fileUrl = documents.URLByAppendingPathComponent(fileName) ?: return null

        return if (data.writeToURL(fileUrl, true)) fileUrl else null
    }
}

@Composable
actual fun provideCameraView(): Camera {
    val handler = koinInject<CameraActionHandler>()
    return IosCamera(handler)
}