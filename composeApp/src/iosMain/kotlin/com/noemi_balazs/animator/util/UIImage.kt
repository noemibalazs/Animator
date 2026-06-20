package com.noemi_balazs.animator.util

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import org.jetbrains.skia.Image
import platform.Foundation.NSData
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation

fun UIImage.toImageBitmap(): ImageBitmap {
    val data: NSData = UIImageJPEGRepresentation(this, 1.0) ?: error("Cannot encode image")

    val bytes = ByteArray(data.length.toInt())
    bytes.usePinned {
        platform.posix.memcpy(
            it.addressOf(0),
            data.bytes,
            data.length
        )
    }

    return Image.makeFromEncoded(bytes).toComposeImageBitmap()
}