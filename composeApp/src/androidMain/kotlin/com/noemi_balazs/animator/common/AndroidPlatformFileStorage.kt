package com.noemi_balazs.animator.common

import android.content.Context
import java.io.File

class AndroidPlatformFileStorage(
    private val context: Context
) : PlatformFileStorage {

    override suspend fun writeTempImage(bytes: ByteArray): String {
        val now = System.currentTimeMillis()
        val file = File(context.cacheDir,  "shared-image-$now.jpg")
        file.writeBytes(bytes)
        return file.absolutePath
    }
}