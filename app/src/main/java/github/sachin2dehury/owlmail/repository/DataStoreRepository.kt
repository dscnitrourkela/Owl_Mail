package github.sachin2dehury.owlmail.repository

import android.content.Context
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.utils.isInternetConnected

class DataStoreRepository(
    private val context: Context,
    private val dataStore: DataStoreExt,
) {

    suspend fun saveLastSync(request: String, lastSync: Long) {
        if (isInternetConnected(context)) {
            dataStore.saveLastSync(Constants.KEY_LAST_SYNC + request, lastSync)
        }
    }

    suspend fun saveCredential(key: String, value: String) = dataStore.saveCredential(key, value)

    suspend fun saveState(key: String, isEnabled: Boolean) =
        dataStore.saveState(key, isEnabled)

    fun readState(key: String) = dataStore.readState(key)

    fun readCredential(key: String) = dataStore.readCredential(key)

    fun readLastSync(request: String) = dataStore.readLastSync(Constants.KEY_LAST_SYNC + request)

    suspend fun resetLogin() {
        saveCredential(Constants.KEY_CREDENTIAL, Constants.NO_CREDENTIAL)
        saveCredential(Constants.KEY_TOKEN, Constants.NO_TOKEN)
        saveState(Constants.KEY_SHOULD_SYNC, false)
        saveLastSync(Constants.KEY_SYNC_SERVICE, Constants.NO_LAST_SYNC)
        saveLastSync(Constants.INBOX_URL, Constants.NO_LAST_SYNC)
        saveLastSync(Constants.SENT_URL, Constants.NO_LAST_SYNC)
        saveLastSync(Constants.DRAFT_URL, Constants.NO_LAST_SYNC)
        saveLastSync(Constants.JUNK_URL, Constants.NO_LAST_SYNC)
        saveLastSync(Constants.TRASH_URL, Constants.NO_LAST_SYNC)
    }
}