package com.noemi_balazs.animator.feature.favorite

import com.noemi_balazs.animator.model.AnimatedCartoon

data class FavoriteUIState(
    val cartoons: List<AnimatedCartoon> = emptyList()
)
