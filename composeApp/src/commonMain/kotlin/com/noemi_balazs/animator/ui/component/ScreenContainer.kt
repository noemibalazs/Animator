package com.noemi_balazs.animator.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ScreenContainer(
    isLoading: Boolean,
    contentAlignment: Alignment = Alignment.TopStart,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = contentAlignment
    ) {

        content()

        if (isLoading) AnimatorProgressBar()
    }
}