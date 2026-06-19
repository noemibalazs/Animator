package com.noemi_balazs.animator.ui.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.noemi_balazs.animator.feature.animator.AnimatorScreen
import com.noemi_balazs.animator.feature.details.DetailsScreen
import com.noemi_balazs.animator.feature.favorite.FavoriteScreen
import com.noemi_balazs.animator.feature.selector.SelectorScreen
import com.noemi_balazs.animator.navigation.Screen
import com.noemi_balazs.animator.navigation.Screen.Animator
import com.noemi_balazs.animator.navigation.Screen.Details
import com.noemi_balazs.animator.navigation.Screen.Favorite
import com.noemi_balazs.animator.navigation.Screen.Selector

@Composable
fun AppNavHost(
    startDestination: Screen,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier.fillMaxSize()
    ) {

        composable<Selector> {
            SelectorScreen(
                onNavigateToAnimator = { path ->
                    navController.navigate(Animator(path))
                }
            )
        }

        composable<Animator> {
            val animator = it.toRoute<Animator>()
            AnimatorScreen(
                path = animator.path,
                onNavigateToFavorite = {
                    navController.navigate(Favorite)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Favorite> {
            FavoriteScreen(
                onNavigateCartoonDetailsScreen = {
                    navController.navigate(Details(it))
                }
            )
        }

        composable<Details> {
            val route = it.toRoute<Details>()
            DetailsScreen(
                cartoonId = route.id,
                onNavigateBack = navController::popBackStack
            )
        }
    }
}