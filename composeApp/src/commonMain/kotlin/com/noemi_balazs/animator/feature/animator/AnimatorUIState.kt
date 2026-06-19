package com.noemi_balazs.animator.feature.animator

import com.noemi_balazs.animator.di.AnimatorImage
import com.noemi_balazs.animator.model.CartoonType

data class AnimatorUIState(
    val currentType: CartoonType? = null,
    val defaultImage: AnimatorImage? = null,
    val animatedImage: AnimatorImage? = null,
    val showAnimatedImage: Boolean = false,
    val showDefaultImage: Boolean = false
)