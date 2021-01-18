package github.sachin2dehury.nitrmail.others

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.createDataStore
import kotlinx.coroutines.flow.first

class DataStoreExt(context: Context) {

    private val dataStore = context.createDataStore(Constants.DATA_STORE_NAME)

    suspend fun saveCredential(key: String, value: String) {
        val dataStoreKey = stringPreferencesKey(key)
        dataStore.edit { settings ->
            settings[dataStoreKey] = value
        }
    }

    suspend fun readCredential(key: String): String? {
        val dataStoreKey = stringPreferencesKey(key)
        val preferences = dataStore.data.first()
        return preferences[dataStoreKey]
    }

    suspend fun saveLastSync(key: String, value: Long) {
        val dataStoreKey = longPreferencesKey(key)
        dataStore.edit { settings ->
            settings[dataStoreKey] = value
        }
    }

    suspend fun readLastSync(key: String): Long {
        val dataStoreKey = longPreferencesKey(key)
        val preferences = dataStore.data.first()
        return preferences[dataStoreKey] ?: Constants.NO_LAST_SYNC
    }
}