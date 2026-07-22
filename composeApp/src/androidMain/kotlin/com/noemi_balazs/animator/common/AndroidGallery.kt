package com.noemi_balazs.animator.common

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.noemi_balazs.animator.utils.ext.toByteArray
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

class AndroidGallery : Gallery {

    @Composable
    override fun GalleryView(
        onSuccess: (String) -> Unit,
        onError: () -> Unit
    ) {
        val fileStorage = koinInject<PlatformFileStorage>()
        val context = LocalContext.current
        val scope = rememberCoroutineScope()

        val launcher =
            rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
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

        LaunchedEffect(Unit) {
            launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }
}

@Composable
actual fun provideGalleryView(): Gallery = AndroidGallery()