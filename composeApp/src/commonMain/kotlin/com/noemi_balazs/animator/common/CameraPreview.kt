package com.noemi_balazs.animator.common

import androidx.compose.runtime.Composable

@Composable
expect fun CameraPreview(
    camera: CameraActionHandler,
    content: @Composable () -> Unit
)