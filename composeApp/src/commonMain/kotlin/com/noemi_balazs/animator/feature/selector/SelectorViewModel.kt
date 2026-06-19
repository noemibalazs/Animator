package com.noemi_balazs.animator.feature.selector

import com.noemi_balazs.animator.base.BaseViewModel
import com.noemi_balazs.animator.data.datastore.AppDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SelectorViewModel(
    private val appDataStore: AppDataStore
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(SelectorUIState())
    val uiState = _uiState.asStateFlow()


    init {
        observeSavedImage()
    }

    private fun observeSavedImage() = runCoroutine {
        appDataStore.getSavedImage().collectFlow { path ->
            updateSharedImage(path)
        }
    }

    fun updateSharedImage(path: String?) = runCoroutine {
        _uiState.update { it.copy(sharedImageUri = path) }
    }
}