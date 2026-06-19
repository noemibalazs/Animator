package com.noemi_balazs.animator.common

import androidx.compose.runtime.Composable

interface Gallery {

    @Composable
    fun GalleryView(
        onSuccess: (String) -> Unit,
        onError: () -> Unit
    )
}

@Composable
expect fun provideGalleryView(): Gallery