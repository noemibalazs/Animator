package com.noemi_balazs.animator.common

import com.noemi_balazs.animator.di.AnimatorImage
import com.noemi_balazs.animator.di.AnimatorView

interface CameraActionHandler {

    fun start()

    fun cameraIsReady(ready: (Boolean) -> Unit)

    fun takePhoto()

    fun onPhotoCaptured(callback: (AnimatorImage) -> Unit)

    fun stop()

    fun getPreviewView(): AnimatorView
}