package com.noemi_balazs.animator.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noemi_balazs.animator.data.datastore.AppDataStore
import com.noemi_balazs.animator.handler.CameraStateHandler
import com.noemi_balazs.animator.model.CameraState
import com.noemi_balazs.animator.resources.Res
import com.noemi_balazs.animator.resources.label_camera_permission
import com.noemi_balazs.animator.resources.label_camera_permission_message
import com.noemi_balazs.animator.ui.component.AnimatorDialog
import com.noemi_balazs.animator.model.CameraPermissionDialogState
import org.koin.compose.koinInject

class IosPermissionHandler : PermissionHandler {

    @Composable
    override fun RequestCameraPermission(onGranted: @Composable (() -> Unit)) {
        val permissionState = koinInject<CameraPermissionDialogState>()
        val dataStore = koinInject<AppDataStore>()

        val state by CameraStateHandler.cameraState.collectAsStateWithLifecycle()
        val wasRequested by dataStore.getCameraPermissionWasRequested()
            .collectAsStateWithLifecycle(false)

        var showDialog by remember { mutableStateOf(false) }
        var isGranted by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            CameraHandlerBridge.handler?.refreshState()
        }

        LaunchedEffect(state, wasRequested) {
            isGranted = state == CameraState.Granted
            if (state != CameraState.Granted && wasRequested == false) {
                CameraHandlerBridge.handler?.requestPermission()
                dataStore.saveCameraPermissionWasRequested()
            } else {
                showDialog = state != CameraState.Granted && wasRequested == true
            }
        }

        if (isGranted) onGranted()

        if (showDialog && permissionState.showCameraPermissionRequest) {
            AnimatorDialog(
                title = Res.string.label_camera_permission,
                message = Res.string.label_camera_permission_message,
                onDismissRequest = {
                    showDialog = false
                    permissionState.showCameraPermissionRequest = false
                },
                onConfirm = {
                    showDialog = false
                    permissionState.showCameraPermissionRequest = false
                    CameraHandlerBridge.handler?.openSettings()
                }
            )
        }
    }
}

@Composable
actual fun providePermissionHandler(): PermissionHandler = IosPermissionHandler()