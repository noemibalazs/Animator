package com.noemi_balazs.animator.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.noemi_balazs.animator.model.AnimatedCartoon

@Database(entities = [AnimatedCartoon::class], version = 1)
@TypeConverters(CartoonTypeConverter::class)
@ConstructedBy(AnimatorDatabaseConstructor::class)
abstract class AnimatorDataBase : RoomDatabase() {

    abstract fun getAnimatorDao(): AnimatorDao
}


@Suppress("KotlinNoActualForExpect")
expect object AnimatorDatabaseConstructor : RoomDatabaseConstructor<AnimatorDataBase> {
    override fun initialize(): AnimatorDataBase
}