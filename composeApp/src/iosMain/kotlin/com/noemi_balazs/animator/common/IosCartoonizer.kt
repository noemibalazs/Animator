package com.noemi_balazs.animator.common

import com.noemi_balazs.animator.di.AnimatorImage
import com.noemi_balazs.animator.model.CartoonType
import platform.UIKit.UIImage

class IosCartoonizer : Cartoonizer {

    override fun createSession(type: CartoonType) {}

    override fun cartoonize(image: AnimatorImage): Result<AnimatorImage> {
        return Result.success(UIImage())
    }

    override fun cleaResources() {}
}