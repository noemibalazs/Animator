package com.noemi_balazs.animator.common

interface PlatformFileStorage {

    suspend fun writeTempImage(bytes: ByteArray): String
}