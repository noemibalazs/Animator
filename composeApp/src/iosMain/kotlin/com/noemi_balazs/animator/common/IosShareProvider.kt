package com.noemi_balazs.animator.common

import androidx.compose.runtime.Composable
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.*
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
class IosShareProvider : ShareProvider {

    override fun share(bytes: ByteArray) {
        val url = saveByteArrayToTempFile(bytes) ?: return

        val controller = UIActivityViewController(
            activityItems = listOf(url),
            applicationActivities = null
        )

        val root = UIApplication.sharedApplication.keyWindow?.rootViewController
        root?.presentViewController(
            controller,
            animated = true,
            completion = null
        )
    }

    private fun saveByteArrayToTempFile(bytes: ByteArray): NSURL? {
        val fileName = "${NSUUID().UUIDString}.jpg"

        val tempDirectory = NSTemporaryDirectory()
        val filePath = tempDirectory + fileName

        val data = bytes.toNSData()

        return if (data.writeToFile(filePath, true)) NSURL.fileURLWithPath(filePath) else null
    }

    private fun ByteArray.toNSData(): NSData =
        usePinned { pinned ->
            NSData.create(
                bytes = pinned.addressOf(0),
                length = size.toULong()
            )
        }
}

@Composable
actual fun provideShareProvider(): ShareProvider = IosShareProvider()