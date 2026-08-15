package com.noemi_balazs.animator.feature.selector

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noemi_balazs.animator.common.provideCameraView
import com.noemi_balazs.animator.common.provideGalleryView
import com.noemi_balazs.animator.common.provideImage
import com.noemi_balazs.animator.common.providePermissionHandler
import com.noemi_balazs.animator.common.provideToastManager
import com.noemi_balazs.animator.resources.Res
import com.noemi_balazs.animator.resources.delete
import com.noemi_balazs.animator.resources.save
import com.noemi_balazs.animator.ui.component.AnimatorButton
import com.noemi_balazs.animator.ui.component.AnimatorImageButton
import com.noemi_balazs.animator.ui.component.ScreenContainer
import org.koin.compose.koinInject

@Composable
fun SelectorScreen(
    onNavigateToAnimator: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = koinInject<SelectorViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ScreenContainer(isLoading = false, modifier = modifier) {
        SelectorScreenContent(
            onNavigateToAnimator = onNavigateToAnimator,
            modifier = modifier,
            uiState = uiState,
            onSharedImageDeleted = {
                viewModel.updateSharedImage(null)
            }
        )
    }
}

@Composable
private fun SelectorScreenContent(
    uiState: SelectorUIState,
    onNavigateToAnimator: (String) -> Unit,
    onSharedImageDeleted: () -> Unit,
    modifier: Modifier = Modifier
) {

    val permissionHandler = providePermissionHandler()
    val cameraProvider = provideCameraView()
    val toastProvider = provideToastManager()
    val galleryView = provideGalleryView()
    val imageProvider = provideImage()

    var onCameraClick by remember { mutableStateOf(false) }
    var onGalleryClick by remember { mutableStateOf(false) }
    var galleryUri by remember { mutableStateOf("") }

    Column(
        modifier = modifier.padding(20.dp).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        SelectorActionRow(
            onCameraClick = {
                onCameraClick = true
                onGalleryClick = false
            },
            onGalleryClick = {
                onGalleryClick = true
                onCameraClick = false
            }
        )

        if (galleryUri.isNotEmpty() && onGalleryClick) {
            val image = imageProvider.provideAnimatorImage(galleryUri)
            imageProvider.DisplayAnimatorImage(
                image = image,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                GalleryActionRow(
                    onSave = {
                        onNavigateToAnimator(galleryUri)
                    },
                    onDelete = {
                        onGalleryClick = false
                    }
                )
            }
        }

        if (onCameraClick) permissionHandler.RequestCameraPermission {
            cameraProvider.CameraView(
                onSuccess = { path ->
                    onNavigateToAnimator(path)
                },
                onError = {
                    toastProvider.showMessage("Something went wrong, try it again!")
                    onCameraClick = false
                }
            )
        }

        if (onGalleryClick) galleryView.GalleryView(
            onError = {
                toastProvider.showMessage("Something went wrong, try it again!")
                onGalleryClick = false
            },
            onSuccess = { uri -> galleryUri = uri }
        )

        if (uiState.sharedImageUri?.isNotBlank() == true) {
            val image = imageProvider.provideAnimatorImage(uiState.sharedImageUri)
            imageProvider.DisplayAnimatorImage(
                image = image,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                GalleryActionRow(
                    onSave = {
                        onNavigateToAnimator(uiState.sharedImageUri)
                    },
                    onDelete = {
                        onSharedImageDeleted()
                    }
                )
            }
        }
    }
}

@Composable
private fun SelectorActionRow(
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit
) {
    Row(
        modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatorButton(
            value = "Gallery",
            onClick = {
                onGalleryClick()
            }
        )

        Spacer(Modifier.padding(12.dp))

        AnimatorButton(
            value = "Camera",
            onClick = {
                onCameraClick()
            }
        )
    }
}

@Composable
private fun GalleryActionRow(
    onSave: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(bottom = 20.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {

        AnimatorImageButton(
            resource = Res.drawable.delete,
            colorTint = MaterialTheme.colorScheme.primary,
            backgroundColor = MaterialTheme.colorScheme.onPrimary,
            onClick = onDelete
        )

        Spacer(Modifier.padding(20.dp))

        AnimatorImageButton(
            resource = Res.drawable.save,
            backgroundColor = MaterialTheme.colorScheme.onPrimary,
            colorTint = MaterialTheme.colorScheme.primary,
            onClick = onSave
        )
    }
}