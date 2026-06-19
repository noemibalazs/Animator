package com.noemi_balazs.animator.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.noemi_balazs.animator.util.CARTOON_TABLE
import com.noemi_balazs.animator.util.ext.formatDate

@Entity(CARTOON_TABLE)
data class AnimatedCartoon(
    @PrimaryKey
    val id: Long,
    val type: CartoonType,
    val imageData: ByteArray
) {
    val label: String
        get() = id.formatDate() + " - ${type.type}"
}
