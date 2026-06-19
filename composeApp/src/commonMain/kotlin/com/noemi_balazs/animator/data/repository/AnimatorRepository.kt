package com.noemi_balazs.animator.data.repository

import com.noemi_balazs.animator.data.database.AnimatorDao
import com.noemi_balazs.animator.model.AnimatedCartoon
import kotlinx.coroutines.flow.Flow

interface AnimatorRepository {

    suspend fun saveCartoon(image: AnimatedCartoon)

    suspend fun deleteCartoon(image: AnimatedCartoon)

    suspend fun getImage(id: Long): AnimatedCartoon

    fun observeCartoons(): Flow<List<AnimatedCartoon>>
}

class AnimatorRepositoryImpl(
    private val animatorDao: AnimatorDao
) : AnimatorRepository {

    override suspend fun saveCartoon(image: AnimatedCartoon) =
        animatorDao.saveCartoon(image)

    override suspend fun deleteCartoon(image: AnimatedCartoon) = animatorDao.removeCartoon(image)

    override suspend fun getImage(id: Long): AnimatedCartoon = animatorDao.getCartoon(id)

    override fun observeCartoons(): Flow<List<AnimatedCartoon>> = animatorDao.observeCartoons()
}