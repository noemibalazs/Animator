package com.noemi_balazs.animator.feature.animator

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noemi_balazs.animator.common.provideImage
import com.noemi_balazs.animator.feature.animator.AnimatorHelper.animatorTypes
import com.noemi_balazs.animator.model.AnimatorType
import com.noemi_balazs.animator.resources.Res
import com.noemi_balazs.animator.resources.back
import com.noemi_balazs.animator.resources.delete
import com.noemi_balazs.animator.resources.edit
import com.noemi_balazs.animator.resources.favorite_unselected
import com.noemi_balazs.animator.ui.component.AnimatorImageButton
import com.noemi_balazs.animator.ui.component.ScreenContainer
import com.noemi_balazs.animator.util.ext.noRippleEffect
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AnimatorScreen(
    path: String? = null,
    onNavigateToFavorite: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val owner = LocalLifecycleOwner.current
    val imageProvider = provideImage()

    val viewModel = koinInject<AnimatorViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var showEditOptions by remember { mutableStateOf(false) }

    DisposableEffect(owner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                viewModel.clearImage()
                viewModel.clearResources()
            }
        }
        owner.lifecycle.addObserver(observer)
        onDispose {
            owner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(path) {
        path?.let {
            val image = imageProvider.provideAnimatorImage(path)
            viewModel.updateCurrentImage(image)
        }
    }

    ScreenContainer(
        isLoading = isLoading,
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {

        if (uiState.defaultImage != null && uiState.showDefaultImage) {
            uiState.defaultImage?.let { image ->
                imageProvider.DisplayAnimatorImage(
                    image = image,
                    modifier = Modifier.fillMaxSize().clickable {
                        showEditOptions = false
                    }
                ) {
                    if (!isLoading) DefaultImageActionRow(
                        onEdit = {
                            showEditOptions = true
                        },
                        onDelete = {
                            viewModel.clearImage()
                            onNavigateBack()
                        }
                    )
                }

                if (showEditOptions) AnimatorTypeSelector(
                    onHideEditOptions = {
                        showEditOptions = false
                    },
                    onTypeSelected = { animatorType ->
                        viewModel.cartoonizeImage(type = animatorType.type)
                    })
            }
        }

        if (uiState.animatedImage != null && uiState.showAnimatedImage) {
            uiState.animatedImage?.let { animatedImage ->
                imageProvider.DisplayAnimatorImage(
                    image = animatedImage,
                    topBarContent = {
                        Box(
                            modifier = Modifier.padding(20.dp).fillMaxWidth(),
                            contentAlignment = Alignment.TopStart
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.back),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                                modifier = modifier
                                    .size(48.dp)
                                    .noRippleEffect(
                                        onClick = {
                                            viewModel.clearImage()
                                            onNavigateBack()
                                        }
                                    )
                            )
                        }
                    },
                    content = {
                        AnimatedImageActionRow(
                            onDelete = viewModel::deleteCartoonizedImage,
                            onSave = {
                                viewModel.saveAnimatedImage(onNavigateToFavorite)
                            }
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun DefaultImageActionRow(
    onEdit: () -> Unit,
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
            resource = Res.drawable.edit,
            backgroundColor = MaterialTheme.colorScheme.onPrimary,
            colorTint = MaterialTheme.colorScheme.primary,
            onClick = onEdit
        )
    }
}

@Composable
private fun AnimatorTypeSelector(
    onHideEditOptions: () -> Unit,
    onTypeSelected: (AnimatorType) -> Unit
) {
    val animatorTypes = animatorTypes()

    LazyRow(
        modifier = Modifier.fillMaxWidth().background(
            shape = RoundedCornerShape(topEnd = 20.dp, topStart = 20.dp),
            color = MaterialTheme.colorScheme.surface
        ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {

        items(
            items = animatorTypes,
            key = { it.name }
        ) { type ->
            AnimatorTypeItem(
                type = type,
                onTypeSelected = {
                    onHideEditOptions()
                    onTypeSelected(type)
                }
            )
        }
    }
}

@Composable
private fun AnimatorTypeItem(
    type: AnimatorType,
    onTypeSelected: () -> Unit
) {
    var isSelected by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.2f else 1f,
        animationSpec = tween(600)
    )

    LaunchedEffect(isSelected) {
        if (isSelected) {
            delay(600)
            onTypeSelected()
        }
    }

    Column(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp).wrapContentSize()
            .noRippleEffect(onClick = {
                isSelected = true
            }),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(type.icon),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier.size(60.dp)
                .scale(scale)
                .padding(3.dp)
        )

        Text(
            text = type.name,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
private fun AnimatedImageActionRow(
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
            resource = Res.drawable.favorite_unselected,
            backgroundColor = MaterialTheme.colorScheme.onPrimary,
            colorTint = MaterialTheme.colorScheme.primary,
            onClick = onSave
        )
    }
}

