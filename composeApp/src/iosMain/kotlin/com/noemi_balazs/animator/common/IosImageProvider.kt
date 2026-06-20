package com.noemi_balazs.animator.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.noemi_balazs.animator.di.AnimatorImage
import com.noemi_balazs.animator.util.toImageBitmap
import platform.UIKit.UIImage
import platform.Foundation.NSURL

class IosImageProvider : ImageProvider {

    @Composable
    override fun DisplayAnimatorImage(
        image: AnimatorImage,
        modifier: Modifier,
        topBarContent: (@Composable () -> Unit)?,
        content: (@Composable () -> Unit)?,
    ) {
        Box(modifier = modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Image(
                    bitmap = image.toImageBitmap(),
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )

                content?.let { content() }
            }

            topBarContent?.let { topBarContent() }
        }
    }

    override fun provideAnimatorImage(path: String): AnimatorImage {
        val filePath = NSURL.URLWithString(path)?.path ?: return UIImage()
        return UIImage.imageWithContentsOfFile(filePath) ?: UIImage()
    }
}

@Composable
actual fun provideImage(): ImageProvider = IosImageProvider()