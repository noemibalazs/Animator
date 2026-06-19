package com.noemi_balazs.animator.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.noemi_balazs.animator.di.AnimatorImage
import platform.UIKit.UIImage

class IosImageProvider : ImageProvider {

    @Composable
    override fun DisplayAnimatorImage(
        image: AnimatorImage,
        modifier: Modifier,
        topBarContent: (@Composable () -> Unit)?,
        content: (@Composable () -> Unit)?,
    ) {}

    override fun provideAnimatorImage(path: String): AnimatorImage {
        return UIImage()
    }
}

@Composable
actual fun provideImage(): ImageProvider = IosImageProvider()