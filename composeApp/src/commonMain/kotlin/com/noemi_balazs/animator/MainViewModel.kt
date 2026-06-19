package com.noemi_balazs.animator

import com.noemi_balazs.animator.base.BaseViewModel
import com.noemi_balazs.animator.data.datastore.AppDataStore

class MainViewModel(
    private val dataStore: AppDataStore
) : BaseViewModel() {

    fun clearImage() = runCoroutine {
        dataStore.saveImage("")
    }

    override fun onCleared() {
        clearImage()
        super.onCleared()
    }
}