package com.noemi_balazs.animator.common

interface CameraHandler {
    fun requestPermission()

    fun refreshState()

    fun openSettings()
}

object CameraHandlerBridge {
    var handler: CameraHandler? = null
}