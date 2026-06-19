package com.noemi_balazs.animator.model

import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.DrawableResource

data class AnimatorType(
    val icon: DrawableResource,
    val name: String,
    val type: CartoonType
)

@Serializable
enum class CartoonType(val type: String) {
    BRYANDLEE("Bryandlee"),
    CELEBA("Caleba"),
    FACE_POINT1("Face point1"),
    FACE_POINT2("Face point2"),
    PAPRIKA("Paprika");
}