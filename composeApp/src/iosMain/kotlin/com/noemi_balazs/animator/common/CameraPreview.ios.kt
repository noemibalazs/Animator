package com.noemi_balazs.animator.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView

@Composable
actual fun CameraPreview(
    camera: CameraActionHandler,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        UIKitView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                camera.getPreviewView()
            }
        )

        content()
    }
}