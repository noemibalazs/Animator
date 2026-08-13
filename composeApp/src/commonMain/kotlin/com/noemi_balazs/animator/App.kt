package com.noemi_balazs.animator

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.noemi_balazs.animator.navigation.Screen
import com.noemi_balazs.animator.ui.component.AppBottomNavigation
import com.noemi_balazs.animator.ui.component.AppNavHost
import com.noemi_balazs.animator.ui.theme.AnimatorTheme
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {

    AnimatorTheme {
        val viewModel = koinInject<MainViewModel>()
        val owner = LocalLifecycleOwner.current

        val navController = rememberNavController()
        val entryState by navController.currentBackStackEntryAsState()
        val currentDestination = entryState?.destination

        DisposableEffect(owner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_STOP) {
                    viewModel.clearImage()
                }
            }
            owner.lifecycle.addObserver(observer)
            onDispose {
                owner.lifecycle.removeObserver(observer)
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                AppBottomNavigation(
                    currentDestination = currentDestination,
                    navHostController = navController
                )
            }
        ) { paddingValues ->
            AppNavHost(
                startDestination = Screen.Selector,
                navController = navController,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}