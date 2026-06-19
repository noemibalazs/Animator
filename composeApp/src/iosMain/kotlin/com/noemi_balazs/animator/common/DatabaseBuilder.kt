package com.noemi_balazs.animator.common

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.noemi_balazs.animator.data.database.AnimatorDataBase
import com.noemi_balazs.animator.util.CARTOON_DB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

fun getDatabase(): AnimatorDataBase {
    return getDatabaseBuilder().build()
}


private fun getDatabaseBuilder(): RoomDatabase.Builder<AnimatorDataBase> {
    val dbFilePath = documentDirectory() + "/$CARTOON_DB"
    return Room.databaseBuilder<AnimatorDataBase>(
        name = dbFilePath,
    ).setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
}

private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path)
}