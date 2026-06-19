package com.noemi_balazs.animator.navigation

import com.noemi_balazs.animator.resources.Res
import com.noemi_balazs.animator.resources.cartoon_selected
import com.noemi_balazs.animator.resources.cartoon_unselected
import com.noemi_balazs.animator.resources.favorite_selected
import com.noemi_balazs.animator.resources.favorite_unselected
import org.jetbrains.compose.resources.DrawableResource
import kotlin.reflect.KClass

enum class NavigationDestination(
    val label: String,
    val screen: Screen,
    val route: KClass<out Screen>,
    val selectedIcon: DrawableResource,
    val unselectedIcon: DrawableResource
) {

    SELECTOR(
        label = "Selector",
        screen = Screen.Selector,
        route = Screen.Selector::class,
        selectedIcon = Res.drawable.cartoon_selected,
        unselectedIcon = Res.drawable.cartoon_unselected
    ),

    FAVORITE(
        label = "Favorite",
        screen = Screen.Favorite,
        route = Screen.Favorite::class,
        selectedIcon = Res.drawable.favorite_selected,
        unselectedIcon = Res.drawable.favorite_unselected
    )
}