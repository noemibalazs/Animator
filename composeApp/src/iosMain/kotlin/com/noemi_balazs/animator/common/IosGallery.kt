package com.noemi_balazs.animator.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import org.koin.compose.koinInject

class IosGallery(private val imagePicker: ImagePicker) : Gallery {

    @Composable
    override fun GalleryView(
        onSuccess: (String) -> Unit,
        onError: () -> Unit
    ) {
        LaunchedEffect(Unit) {
            val uri = imagePicker.pickImage()
            uri?.let { onSuccess(uri) } ?: onError()
        }
    }
}

@Composable
actual fun provideGalleryView(): Gallery {
    val picker = koinInject<ImagePicker>()
    return IosGallery(picker)
}