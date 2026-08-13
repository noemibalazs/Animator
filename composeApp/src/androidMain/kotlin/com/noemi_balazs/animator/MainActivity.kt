package com.noemi_balazs.animator

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.IntentCompat
import com.noemi_balazs.animator.common.SharedMediaManager
import com.noemi_balazs.animator.utils.ext.toByteArray

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        handleSharedIntent(intent)

        setContent {
            App()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleSharedIntent(intent)
    }

    private fun handleSharedIntent(intent: Intent) {
        if (intent.action != Intent.ACTION_SEND) return
        val type = intent.type ?: return
        if (!type.startsWith("image/")) return

        val uri =
            IntentCompat.getParcelableExtra(intent, Intent.EXTRA_STREAM, Uri::class.java) ?: return
        saveImage(uri)

        setIntent(Intent())
    }

    private fun saveImage(uri: Uri) {
        val bytes = uri.toByteArray(this)
        bytes?.let { SharedMediaManager.handleSharedUrl(it) }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}