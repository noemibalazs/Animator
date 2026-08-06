package com.noemi_balazs.animator.common

import com.noemi_balazs.animator.di.AnimatorImage
import com.noemi_balazs.animator.model.CartoonType
import com.noemi_balazs.animator.onnx.OnnxEngine

class IosCartoonizer : Cartoonizer {

    private val engine by lazy { OnnxEngine }

    override fun createSession(type: CartoonType) {
        engine.createSession(type)
    }

    override fun cartoonize(image: AnimatorImage): Result<AnimatorImage> =
        engine.animate(image)

    override fun cleaResources() {
        engine.closeResources()
    }
}