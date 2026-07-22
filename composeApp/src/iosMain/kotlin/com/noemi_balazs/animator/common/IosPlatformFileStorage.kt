package com.noemi_balazs.animator.common

import com.noemi_balazs.animator.util.toNSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.*
import kotlin.time.Clock

class IosPlatformFileStorage : PlatformFileStorage {

    override suspend fun writeTempImage(bytes: ByteArray): String {
        val documentsUrl = (
                NSFileManager.defaultManager.URLsForDirectory(
                    directory = NSDocumentDirectory,
                    inDomains = NSUserDomainMask
                ).firstOrNull() as? NSURL)
            ?: error("Animator -documents directory not found")


        val now = Clock.System.now().toEpochMilliseconds()
        val fileUrl = documentsUrl.URLByAppendingPathComponent("shared-image-$now.jpg")
            ?: error("Animator - URL not found")

        val data = bytes.toNSData()

        check(data.writeToURL(fileUrl, atomically = true)) {
            "Animator - failed to write image"
        }

        return fileUrl.path ?: error("Animator - missing file path")
    }
}