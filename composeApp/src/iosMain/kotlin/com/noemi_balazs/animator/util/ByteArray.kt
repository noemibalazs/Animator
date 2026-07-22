package com.noemi_balazs.animator.util

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.dataWithBytes

fun ByteArray.toNSData(): NSData =
    usePinned {
        NSData.dataWithBytes(
            bytes = it.addressOf(0),
            length = size.toULong()
        )
    }