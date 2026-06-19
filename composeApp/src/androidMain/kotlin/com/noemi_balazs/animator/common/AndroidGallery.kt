package com.noemi_balazs.animator.common

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

class AndroidGallery : Gallery {

    @Composable
    override fun GalleryView(
        onSuccess: (String) -> Unit,
        onError: () -> Unit
    ) {

        val launcher =
            rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                uri?.let { onSuccess(uri.toString()) } ?: onError()
            }

        LaunchedEffect(Unit) {
            launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }
}

@Composable
actual fun provideGalleryView(): Gallery = AndroidGallery()