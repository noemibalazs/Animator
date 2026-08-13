package com.noemi_balazs.animator.feature.favorite

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.noemi_balazs.animator.model.AnimatedCartoon
import com.noemi_balazs.animator.resources.Res
import com.noemi_balazs.animator.resources.open
import com.noemi_balazs.animator.ui.component.AnimatorImageButton
import com.noemi_balazs.animator.ui.component.ScreenContainer
import com.noemi_balazs.animator.util.ext.noRippleEffect
import com.noemi_balazs.animator.util.ext.semibold
import org.koin.compose.koinInject

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FavoriteScreen(
    onNavigateBack: () -> Unit,
    onNavigateCartoonDetailsScreen: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = koinInject<FavoriteViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    BackHandler {
        onNavigateBack()
    }

    ScreenContainer(
        isLoading = isLoading,
        modifier = modifier
    ) {
        FavoriteScreenContent(
            cartoons = uiState.cartoons,
            onCartoonClicked = onNavigateCartoonDetailsScreen
        )
    }
}

@Composable
private fun FavoriteScreenContent(
    cartoons: List<AnimatedCartoon>,
    onCartoonClicked: (Long) -> Unit,
    modifier: Modifier = Modifier
) {

    LazyColumn(
        modifier = modifier.padding(20.dp).fillMaxSize(),
        state = rememberLazyListState(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        items(
            items = cartoons,
            key = { it.id }
        ) { cartoon ->
            AnimatedCartoonItem(
                item = cartoon,
                onClick = {
                    onCartoonClicked(cartoon.id)
                }
            )
        }
    }
}

@Composable
private fun AnimatedCartoonItem(
    item: AnimatedCartoon,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier.padding(vertical = 12.dp).fillMaxWidth()
            .noRippleEffect(onClick = onClick),
    ) {
        Text(
            text = item.label,
            style = MaterialTheme.typography.titleLarge.semibold.copy(
                color = MaterialTheme.colorScheme.secondary,
            ),
            modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth()
        )

        Box(
            modifier = Modifier.fillMaxSize().height(390.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            AsyncImage(
                model = item.imageData,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            AnimatorImageButton(
                modifier = Modifier.padding(16.dp),
                resource = Res.drawable.open,
                colorTint = MaterialTheme.colorScheme.primary,
                backgroundColor = MaterialTheme.colorScheme.onPrimary,
                onClick = onClick
            )
        }
    }
}