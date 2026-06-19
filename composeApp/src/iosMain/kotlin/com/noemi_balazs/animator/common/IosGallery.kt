package com.noemi_balazs.animator.common

import androidx.compose.runtime.Composable

class IosGallery : Gallery {

    @Composable
    override fun GalleryView(
        onSuccess: (String) -> Unit,
        onError: () -> Unit
    ) {}
}

@Composable
actual fun provideGalleryView(): Gallery = IosGallery()