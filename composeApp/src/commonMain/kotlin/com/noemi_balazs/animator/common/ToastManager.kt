package com.noemi_balazs.animator.common

import androidx.compose.runtime.Composable

interface ToastManager {

    fun showMessage(message: String, isShortDuration: Boolean = true)
}

@Composable
expect fun provideToastManager(): ToastManager