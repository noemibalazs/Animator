package com.noemi_balazs.animator.common

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.noemi_balazs.animator.data.database.AnimatorDataBase
import com.noemi_balazs.animator.util.CARTOON_DB
import kotlinx.coroutines.Dispatchers

fun getDatabase(context: Context): AnimatorDataBase {
    return getDatabaseBuilder(context).build()
}

private fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<AnimatorDataBase> {
    val appContext = context.applicationContext
    val filePath = appContext.getDatabasePath(CARTOON_DB)
    return Room.databaseBuilder<AnimatorDataBase>(
        appContext,
        filePath.absolutePath
    ).setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
}