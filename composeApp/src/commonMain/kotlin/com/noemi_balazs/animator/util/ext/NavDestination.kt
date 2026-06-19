package com.noemi_balazs.animator.util.ext

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import kotlin.reflect.KClass

fun NavDestination?.isOnRoute(route: KClass<*>): Boolean =
    this?.hierarchy?.any { dest -> dest.hasRoute(route) } == true


fun NavDestination?.isOnAnyRoute(vararg routes: KClass<*>): Boolean {
    return this?.hierarchy?.any { dest -> routes.any { route -> dest.hasRoute(route) } } == true
}