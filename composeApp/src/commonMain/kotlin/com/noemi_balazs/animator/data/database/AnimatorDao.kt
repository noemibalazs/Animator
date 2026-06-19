package com.noemi_balazs.animator.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.noemi_balazs.animator.model.AnimatedCartoon
import com.noemi_balazs.animator.util.CARTOON_TABLE
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimatorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCartoon(cartoon: AnimatedCartoon)

    @Query("SELECT * FROM $CARTOON_TABLE WHERE id = :id")
    suspend fun getCartoon(id: Long): AnimatedCartoon

    @Query("SELECT * FROM $CARTOON_TABLE")
    fun observeCartoons(): Flow<List<AnimatedCartoon>>

    @Delete
    suspend fun removeCartoon(cartoon: AnimatedCartoon)
}