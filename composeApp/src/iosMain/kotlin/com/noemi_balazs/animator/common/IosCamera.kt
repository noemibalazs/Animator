package com.noemi_balazs.animator.common

import androidx.compose.runtime.Composable

class IosCamera : Camera {

    @Composable
    override fun CameraView(onSuccess: (String) -> Unit, onError: () -> Unit) {
    }
}

@Composable
actual fun provideCameraView(): Camera = IosCamera()