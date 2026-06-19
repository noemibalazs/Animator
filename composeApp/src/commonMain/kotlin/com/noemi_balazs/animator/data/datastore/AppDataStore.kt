package com.noemi_balazs.animator.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AppDataStore(private val dataStore: DataStore<Preferences>) {

    suspend fun saveImage(value: String) = withContext(Dispatchers.IO) {
        dataStore.edit { preferences -> preferences[KEY_SHARED_IMAGE] = value }
    }

    fun getSavedImage(): Flow<String?> = dataStore.data
        .map { preferences -> preferences[KEY_SHARED_IMAGE] }
        .flowOn(Dispatchers.IO)

    companion object {

        private val KEY_SHARED_IMAGE = stringPreferencesKey("key_shared_image")
    }
}