package com.noemi_balazs.animator.feature.animator

import com.noemi_balazs.animator.model.AnimatorType
import com.noemi_balazs.animator.model.CartoonType
import com.noemi_balazs.animator.resources.Res
import com.noemi_balazs.animator.resources.icon1
import com.noemi_balazs.animator.resources.icon2
import com.noemi_balazs.animator.resources.icon3
import com.noemi_balazs.animator.resources.icon4
import com.noemi_balazs.animator.resources.icon5

object AnimatorHelper {

    fun animatorTypes(): List<AnimatorType> {
        val types = CartoonType.entries
        val resources = listOf(
            Res.drawable.icon1,
            Res.drawable.icon2,
            Res.drawable.icon3,
            Res.drawable.icon4,
            Res.drawable.icon5
        )
        return CartoonType.entries.mapIndexed { index, type ->
            AnimatorType(
                icon = resources[index],
                name = type.type,
                type = types[index]
            )
        }
    }
}