package com.noemi_balazs.animator.common

import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.CameraSelector
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.camera.view.CameraController.IMAGE_CAPTURE
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil3.compose.AsyncImage
import com.noemi_balazs.animator.camera.CameraUtil.saveToMediaStore
import com.noemi_balazs.animator.camera.CameraUtil.takePhoto
import com.noemi_balazs.animator.resources.Res
import com.noemi_balazs.animator.resources.camera
import com.noemi_balazs.animator.resources.delete
import com.noemi_balazs.animator.resources.save
import com.noemi_balazs.animator.ui.component.AnimatorImageButton
import com.noemi_balazs.animator.utils.ext.toByteArray
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

class AndroidCamera : Camera {

    @Composable
    override fun CameraView(
        onSuccess: (String) -> Unit,
        onError: () -> Unit
    ) {
        val context = LocalContext.current
        val owner = LocalLifecycleOwner.current
        val scope = rememberCoroutineScope()
        val fileStorage = koinInject<PlatformFileStorage>()

        val cameraController = remember {
            LifecycleCameraController(context).apply {
                cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                setEnabledUseCases(IMAGE_CAPTURE)
                isTapToFocusEnabled = true
                isPinchToZoomEnabled = true
            }
        }

        var takePhoto by remember { mutableStateOf(true) }
        var cameraResult by remember { mutableStateOf<Bitmap?>(null) }

        LaunchedEffect(owner) {
            cameraController.bindToLifecycle(owner)
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            when (takePhoto) {
                true -> {
                    TakePhotoContent(
                        cameraController = cameraController,
                        context = context,
                        onResult = { bitmap ->
                            cameraResult = bitmap
                            takePhoto = false
                        }
                    )
                }

                else -> {
                    cameraResult?.let { bitmap ->
                        CameraResultContent(
                            bitmap = bitmap,
                            onSave = {
                                saveToMediaStore(
                                    bitmap = bitmap,
                                    contentResolver = context.contentResolver,
                                ) { uri ->
                                    cameraResult = null
                                    uri?.let {
                                        val bytes = uri.toByteArray(context)
                                        bytes?.let {
                                           scope.launch {
                                               val path = fileStorage.writeTempImage(bytes)
                                               onSuccess(path)
                                           }
                                        }
                                    } ?: onError()
                                }
                            },
                            onDelete = {
                                cameraResult = null
                                takePhoto = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TakePhotoContent(
    cameraController: LifecycleCameraController,
    context: Context,
    onResult: (Bitmap) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize(),
            factory = { context ->
                PreviewView(context).apply {
                    controller = cameraController
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
            }
        )

        TakePhotoActionRow(
            context = context,
            controller = cameraController
        ) { bitmap -> onResult(bitmap) }
    }
}

@Composable
private fun TakePhotoActionRow(
    controller: LifecycleCameraController,
    context: Context,
    onResult: (Bitmap) -> Unit
) {
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
                onClick = {
                    takePhoto(controller, context) {
                        onResult(it)
                    }
                },
                colorTint = MaterialTheme.colorScheme.primary,
                backgroundColor = Color.White,
                resource = Res.drawable.camera
            )
        }
    }
}

@Composable
private fun CameraResultContent(
    bitmap: Bitmap,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {

        AsyncImage(
            model = bitmap,
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

@Composable
actual fun provideCameraView(): Camera = AndroidCamera()