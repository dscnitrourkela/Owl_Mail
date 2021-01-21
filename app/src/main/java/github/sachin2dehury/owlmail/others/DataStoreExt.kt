package github.sachin2dehury.owlmail.others

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.createDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class DataStoreExt(context: Context) {

    private val dataStore = context.createDataStore(Constants.DATA_STORE_NAME)

    suspend fun saveCredential(key: String, value: String) {
        dataStore.edit { settings ->
            settings[stringPreferencesKey(key)] = value
        }
    }

    suspend fun saveLastSync(key: String, value: Long) {
        dataStore.edit { settings ->
            settings[longPreferencesKey(key)] = value
        }
    }

    suspend fun saveDarkTheme(key: String, value: Boolean) {
        dataStore.edit { settings ->
            settings[booleanPreferencesKey(key)] = value
        }
    }

    fun readCredential(key: String) = flow {
        emit(dataStore.data.first()[stringPreferencesKey(key)])
    }

    fun readLastSync(key: String) = flow {
        emit(dataStore.data.first()[longPreferencesKey(key)])
    }

    fun readDarkTheme(key: String) = flow {
        emit(dataStore.data.first()[booleanPreferencesKey(key)])
    }
}