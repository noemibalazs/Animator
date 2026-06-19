package com.noemi_balazs.animator.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import com.noemi_balazs.animator.navigation.NavigationDestination
import com.noemi_balazs.animator.navigation.Screen
import com.noemi_balazs.animator.util.ext.isOnAnyRoute
import com.noemi_balazs.animator.util.ext.isOnRoute
import org.jetbrains.compose.resources.painterResource

@Composable
fun AppBottomNavigation(
    currentDestination: NavDestination?,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {

    val visible = currentDestination.isOnAnyRoute(
        Screen.Selector::class, Screen.Favorite::class
    )

    AnimatedVisibility(visible = visible) {

        NavigationBar(
            containerColor = colorScheme.surface,
            contentColor = colorScheme.onSurface,
            tonalElevation = 12.dp,
            modifier = modifier.fillMaxWidth()
        ) {

            NavigationDestination.entries.forEach { destination ->

                val isSelected = currentDestination.isOnRoute(destination.route)

                NavigationBarItem(
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                    ),
                    selected = isSelected,
                    label = {
                        Text(
                            text = destination.label,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            ),
                            color = colorScheme.onPrimaryContainer
                        )
                    },
                    onClick = {
                        navHostController.navigate(destination.screen) {
                            launchSingleTop = true
                            restoreState = false
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(
                                if (isSelected) destination.selectedIcon else destination.unselectedIcon
                            ),
                            modifier = Modifier.size(24.dp),
                            contentDescription = null
                        )
                    }
                )
            }
        }
    }
}