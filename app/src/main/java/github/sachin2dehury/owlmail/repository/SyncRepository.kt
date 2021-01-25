package github.sachin2dehury.owlmail.repository

import android.content.Context
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import github.sachin2dehury.owlmail.api.calls.BasicAuthInterceptor
import github.sachin2dehury.owlmail.api.calls.MailApi
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.others.Resource
import github.sachin2dehury.owlmail.utilities.isInternetConnected
import kotlinx.coroutines.flow.flow

class SyncRepository(
    private val basicAuthInterceptor: BasicAuthInterceptor,
    private val context: Context,
    private val dataStore: DataStoreExt,
    private val mailApi: MailApi,
) {

    fun getMails(request: String, search: String) = flow {
        val result = if (isInternetConnected(context)) {
            Resource.success(mailApi.getMails(request, search))
        } else {
            Resource.error("No Internet", null)
        }
        emit(result)
    }

    suspend fun saveLastSync(request: String, lastSync: Long) =
        dataStore.saveLastSync(Constants.KEY_LAST_SYNC + request, lastSync)

    fun readLastSync(request: String) = dataStore.readLastSync(Constants.KEY_LAST_SYNC + request)

    fun readState(key: String) = dataStore.readState(key)

    fun getAuthCredentials() =
        dataStore.readCredential(Constants.KEY_TOKEN).asLiveData().map { token ->
            if (token != null && token != Constants.NO_TOKEN) {
                basicAuthInterceptor.token = token
                true
            } else {
                false
            }
        }.switchMap { isLoggedIn ->
            dataStore.readCredential(Constants.KEY_CREDENTIAL).asLiveData().map { credential ->
                if (credential != null && credential != Constants.NO_TOKEN) {
                    basicAuthInterceptor.credential = credential
                    true
                } else {
                    isLoggedIn
                }
            }
        }
}