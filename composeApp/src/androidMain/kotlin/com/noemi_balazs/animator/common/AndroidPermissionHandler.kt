package com.noemi_balazs.animator.common

import android.Manifest.permission.CAMERA
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.noemi_balazs.animator.resources.Res
import com.noemi_balazs.animator.resources.label_camera_permission
import com.noemi_balazs.animator.resources.label_camera_permission_message
import com.noemi_balazs.animator.resources.label_camera_permission_required
import com.noemi_balazs.animator.resources.label_camera_settings
import com.noemi_balazs.animator.ui.component.AnimatorDialog

class AndroidPermissionHandler : PermissionHandler {

    @Composable
    override fun RequestCameraPermission(onGranted: @Composable () -> Unit) {

        val context = LocalContext.current
        val activity = LocalActivity.current

        var isPermissionGranted by remember {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context, CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            )
        }
        var shouldShowRationale by remember { mutableStateOf(false) }
        var showSettings by remember { mutableStateOf(true) }
        var askedOnce by remember { mutableStateOf(false) }

        val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            isPermissionGranted = isGranted
            askedOnce = true

            if (!isGranted) {
                activity?.let {
                    shouldShowRationale =
                        ActivityCompat.shouldShowRequestPermissionRationale(activity, CAMERA)
                }
            }
        }

        val permanentlyDenied = !isPermissionGranted && !shouldShowRationale && askedOnce

        LaunchedEffect(Unit) {
            if (!isPermissionGranted && !shouldShowRationale && !permanentlyDenied) {
                launcher.launch(CAMERA)
            }
        }

        when {
            isPermissionGranted -> onGranted()
            shouldShowRationale -> {
                AnimatorDialog(
                    title = Res.string.label_camera_permission,
                    message = Res.string.label_camera_permission_message,
                    onDismissRequest = {
                        shouldShowRationale = false
                    },
                    onConfirm = {
                        launcher.launch(CAMERA)
                    }
                )
            }

            permanentlyDenied -> {
                if (showSettings) AnimatorDialog(
                    title = Res.string.label_camera_permission_required,
                    message = Res.string.label_camera_settings,
                    onDismissRequest = {
                        showSettings = false
                    },
                    onConfirm = {
                        openSettings(context)
                    }
                )
            }
        }
    }

    private fun openSettings(context: Context) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        )
        context.startActivity(intent)
    }
}

@Composable
actual fun providePermissionHandler(): PermissionHandler = AndroidPermissionHandler()
