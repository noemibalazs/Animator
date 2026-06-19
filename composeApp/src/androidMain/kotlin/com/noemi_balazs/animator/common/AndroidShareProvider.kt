package com.noemi_balazs.animator.common

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

class AndroidShareProvider(
    private val context: Context
) : ShareProvider {

    override fun share(bytes: ByteArray) {
        val tempFile = File(context.cacheDir, "share_animated_image.jpg")
        tempFile.outputStream().use { stream ->
            stream.write(bytes)
        }
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempFile
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/png"
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Image"))
    }
}

@Composable
actual fun provideShareProvider(): ShareProvider {
    val context = LocalContext.current
    return AndroidShareProvider(context)
}

