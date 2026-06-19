package com.noemi_balazs.animator.data.database

import androidx.room.TypeConverter
import com.noemi_balazs.animator.model.CartoonType

class CartoonTypeConverter {

    @TypeConverter
    fun fromCartoonType(type: CartoonType): String = type.name

    @TypeConverter
    fun toCartoonType(value: String): CartoonType =
        CartoonType.valueOf(value)
}