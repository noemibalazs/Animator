package com.noemi_balazs.animator.common

import android.content.Context
import android.graphics.ImageDecoder
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import com.noemi_balazs.animator.di.AnimatorImage

class AndroidImageProvider(
    private val context: Context
) : ImageProvider {

    @Composable
    override fun DisplayAnimatorImage(
        image: AnimatorImage,
        modifier: Modifier,
        topBarContent: (@Composable () -> Unit)?,
        content: (@Composable () -> Unit)?
    ) {
        Box(
            modifier = modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                AsyncImage(
                    model = image,
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
        val source = ImageDecoder.createSource(
            context.contentResolver,
            path.toUri()
        )

        return ImageDecoder.decodeBitmap(source)
    }
}

@Composable
actual fun provideImage(): ImageProvider {
    val context = LocalContext.current
    return AndroidImageProvider(context)
}
