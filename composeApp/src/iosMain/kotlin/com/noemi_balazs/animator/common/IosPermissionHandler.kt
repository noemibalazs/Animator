package com.noemi_balazs.animator.common

import androidx.compose.runtime.Composable

class IosPermissionHandler : PermissionHandler {

    @Composable
    override fun RequestCameraPermission(onGranted: @Composable (() -> Unit)) {}
}

@Composable
actual fun providePermissionHandler(): PermissionHandler = IosPermissionHandler()