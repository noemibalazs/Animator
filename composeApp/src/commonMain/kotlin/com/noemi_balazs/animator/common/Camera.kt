package com.noemi_balazs.animator.common

import androidx.compose.runtime.Composable

interface Camera {

    @Composable
    fun CameraView(
        onSuccess: (String) -> Unit,
        onError: () -> Unit
    )
}

@Composable
expect fun provideCameraView(): Camera