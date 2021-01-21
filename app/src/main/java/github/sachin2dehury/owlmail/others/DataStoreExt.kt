package github.sachin2dehury.owlmail.others

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class DataStoreExt(context: Context) {

    private val dataStore = context.createDataStore(Constants.DATA_STORE_NAME)

    suspend fun saveCredential(key: String, value: String) {
        val dataStoreKey = stringPreferencesKey(key)
        dataStore.edit { settings ->
            settings[dataStoreKey] = value
        }
    }

    fun readCredential(key: String) = flow {
        val dataStoreKey = stringPreferencesKey(key)
        emit(dataStore.data.first()[dataStoreKey])
    }.asLiveData().value

    suspend fun saveLastSync(key: String, value: Long) {
        val dataStoreKey = longPreferencesKey(key)
        dataStore.edit { settings ->
            settings[dataStoreKey] = value
        }
    }

    fun readLastSync(key: String) = flow {
        val dataStoreKey = longPreferencesKey(key)
        emit(dataStore.data.first()[dataStoreKey])
    }.asLiveData().value ?: Constants.NO_LAST_SYNC
}