package com.noemi_balazs.animator.handler

import com.noemi_balazs.animator.model.CameraState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object CameraStateHandler {

    private var _cameraState = MutableStateFlow<CameraState?>(null)
    val cameraState = _cameraState.asStateFlow()

    fun setCameraState(state: String) {
        val mappedValue = state.replaceFirstChar { it.uppercase() }
        val cameraStateValue = CameraState.valueOf(mappedValue)
        _cameraState.value = cameraStateValue
    }

    fun clearState() {
        _cameraState.value = null
    }
}