package com.noemi_balazs.animator.feature.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.noemi_balazs.animator.common.provideShareProvider
import com.noemi_balazs.animator.model.AnimatedCartoon
import com.noemi_balazs.animator.resources.Res
import com.noemi_balazs.animator.resources.delete
import com.noemi_balazs.animator.resources.share
import com.noemi_balazs.animator.ui.component.AnimatorImageButton
import com.noemi_balazs.animator.ui.component.ScreenContainer
import org.koin.compose.koinInject

@Composable
fun DetailsScreen(
    cartoonId: Long?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = koinInject<DetailsViewModel>()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val shareProvider = provideShareProvider()

    LaunchedEffect(cartoonId) {
        cartoonId?.let { viewModel.loadAnimatedCartoon(it) }
    }

    ScreenContainer(
        isLoading = isLoading,
        modifier = modifier.fillMaxSize()
    ) {

        uiState.cartoon?.let { cartoon ->
            DetailsScreenContainer(
                cartoon = cartoon,
                onDeleteClicked = {
                    viewModel.deleteCartoon(cartoon, onNavigateBack)
                },
                onShareClicked = {
                    shareProvider.share(cartoon.imageData)
                }
            )
        }
    }
}

@Composable
private fun DetailsScreenContainer(
    cartoon: AnimatedCartoon,
    onShareClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier.padding(20.dp).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {

        AsyncImage(
            model = cartoon.imageData,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().weight(1f)
        )

        Row(
            modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom
        ) {

            AnimatorImageButton(
                resource = Res.drawable.delete,
                colorTint = MaterialTheme.colorScheme.primary,
                backgroundColor = Color.Transparent,
                onClick = onDeleteClicked
            )

            Spacer(Modifier.padding(16.dp))

            AnimatorImageButton(
                resource = Res.drawable.share,
                backgroundColor = Color.Transparent,
                colorTint = MaterialTheme.colorScheme.primary,
                onClick = onShareClicked
            )
        }
    }
}