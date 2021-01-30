package github.sachin2dehury.owlmail.services

import android.text.format.DateUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.others.Event
import github.sachin2dehury.owlmail.others.Resource
import github.sachin2dehury.owlmail.repository.DataStoreRepository
import github.sachin2dehury.owlmail.repository.MailRepository

class SyncWorker(
    val alarmBroadcast: AlarmBroadcast,
    private val dataStoreRepository: DataStoreRepository,
    private val mailRepository: MailRepository,
    val notificationExt: NotificationExt,
) {

    private val _forceUpdate = MutableLiveData(false)

    val isLoggedIn = _forceUpdate.switchMap {
        dataStoreRepository.readCredential(Constants.KEY_TOKEN).asLiveData().map { token ->
            if (token != null && token != Constants.NO_TOKEN) {
                mailRepository.setToken(token)
                true
            } else {
                false
            }
        }.switchMap { loggedIn ->
            dataStoreRepository.readCredential(Constants.KEY_CREDENTIAL).asLiveData()
                .map { credential ->
                    if (credential != null && credential != Constants.NO_TOKEN) {
                        mailRepository.setCredential(credential)
                        true
                    } else {
                        loggedIn
                    }
                }
        }
    }

    val shouldUpdate = isLoggedIn.switchMap {
        dataStoreRepository.readState(Constants.KEY_SHOULD_SYNC).asLiveData().map { it ?: false }
    }

    val lastSync = shouldUpdate.switchMap {
        dataStoreRepository.readLastSync(Constants.KEY_SYNC_SERVICE).asLiveData()
            .map { it ?: System.currentTimeMillis() }
    }

    val mails = lastSync.switchMap {
        if (shouldUpdate.value == true) {
            mailRepository.getMails(Constants.INBOX_URL, Constants.UPDATE_QUERY + lastSync)
                .asLiveData().switchMap { MutableLiveData(Event(it)) }
        } else {
            MutableLiveData(Event(Resource.error("DO NOT SYNC", null)))
        }
    }

    suspend fun saveLastSync() = dataStoreRepository.saveLastSync(
        Constants.KEY_SYNC_SERVICE,
        System.currentTimeMillis() - DateUtils.MINUTE_IN_MILLIS
    )

    fun refresh() = _forceUpdate.postValue(true)
}