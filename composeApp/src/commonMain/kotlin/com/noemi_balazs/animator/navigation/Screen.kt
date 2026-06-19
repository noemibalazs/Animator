package com.noemi_balazs.animator.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {

    @Serializable
    data object Selector : Screen

    @Serializable
    data class Animator(val path: String? = null) : Screen

    @Serializable
    data object Favorite : Screen

    @Serializable
    data class Details(val id: Long? = null) : Screen
}