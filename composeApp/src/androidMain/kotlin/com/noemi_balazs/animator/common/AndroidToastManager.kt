package com.noemi_balazs.animator.common

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

class AndroidToastManager(private val context: Context) : ToastManager {

    override fun showMessage(message: String, isShortDuration: Boolean) {
        val length = if (isShortDuration) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
        Toast.makeText(context, message, length).show()
    }
}

@Composable
actual fun provideToastManager(): ToastManager {
    val context = LocalContext.current
    return AndroidToastManager(context)
}