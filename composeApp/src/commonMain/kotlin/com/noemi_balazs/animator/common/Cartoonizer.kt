package com.noemi_balazs.animator.common

import com.noemi_balazs.animator.di.AnimatorImage
import com.noemi_balazs.animator.model.CartoonType

interface Cartoonizer {

    fun createSession(type: CartoonType)

    fun cartoonize(image: AnimatorImage): Result<AnimatorImage>

    fun cleaResources()
}