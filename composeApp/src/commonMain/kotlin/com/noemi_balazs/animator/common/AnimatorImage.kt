package com.noemi_balazs.animator.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.noemi_balazs.animator.di.AnimatorImage

interface ImageProvider {

    @Composable
    fun DisplayAnimatorImage(
        image: AnimatorImage,
        modifier: Modifier = Modifier,
        topBarContent: (@Composable () -> Unit)? = null,
        content: (@Composable () -> Unit)? = null
    )

    fun provideAnimatorImage(
        path: String
    ): AnimatorImage
}

@Composable
expect fun provideImage(): ImageProvider