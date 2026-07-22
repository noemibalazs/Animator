package com.noemi_balazs.animator.common

import com.noemi_balazs.animator.data.datastore.AppDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin

object SharedMediaManager {

    private val appDataStore: AppDataStore = getKoin().get()
    private val scope: CoroutineScope = getKoin().get()
    private val fileStorage: PlatformFileStorage = getKoin().get()

    fun handleSharedUrl(
        bytes: ByteArray,
        error: Throwable? = null
    ) {
        if (error != null) return
        scope.launch {
            val path = fileStorage.writeTempImage(bytes)
            appDataStore.saveImage(path)
        }
    }
}