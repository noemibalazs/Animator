package com.noemi_balazs.animator.common

import androidx.compose.runtime.Composable

interface PermissionHandler {

    @Composable
    fun RequestCameraPermission(
        onGranted: @Composable () -> Unit
    )
}

@Composable
expect fun providePermissionHandler(): PermissionHandler