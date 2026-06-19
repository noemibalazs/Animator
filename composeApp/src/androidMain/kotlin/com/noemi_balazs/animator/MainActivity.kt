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
import androidx.lifecycle.lifecycleScope
import com.noemi_balazs.animator.data.datastore.AppDataStore
import kotlinx.coroutines.launch
import org.koin.android.ext.android.getKoin

class MainActivity : ComponentActivity() {

    private val dataStore: AppDataStore by lazy { getKoin().get() }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            App()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        val type = intent.type ?: return

        if (intent.action == Intent.ACTION_SEND && type.startsWith("image")) {
            val uri = IntentCompat.getParcelableExtra(intent, Intent.EXTRA_STREAM, Uri::class.java)
            saveImage(uri)
        }
    }

    private fun saveImage(uri: Uri?) {
        uri?.let { uri ->
            lifecycleScope.launch {
                dataStore.saveImage(uri.toString())
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}