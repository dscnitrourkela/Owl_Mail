package github.sachin2dehury.owlmail.services

import android.content.Intent
import android.text.format.DateUtils
import androidx.lifecycle.*
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.owlmail.api.data.Mail
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.others.Event
import github.sachin2dehury.owlmail.others.Status
import github.sachin2dehury.owlmail.others.debugLog
import github.sachin2dehury.owlmail.repository.SyncRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SyncService : LifecycleService() {

    @Inject
    lateinit var syncRepository: SyncRepository

    @Inject
    lateinit var notificationExt: NotificationExt

    @Inject
    lateinit var alarmBroadcast: AlarmBroadcast

    private val shouldUpdate = MutableLiveData(false)

    private val lastSync = MutableLiveData(System.currentTimeMillis())

    override fun onCreate() {
        super.onCreate()
        syncRepository.getAuthCredentials()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        readSavedData()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun readSavedData() = CoroutineScope(Dispatchers.IO).launch {
        shouldUpdate.postValue(syncRepository.readState(Constants.KEY_SHOULD_SYNC).first() ?: false)
        lastSync.postValue(
            syncRepository.readLastSync(Constants.KEY_SYNC_SERVICE).first()
                ?: System.currentTimeMillis()
        )
    }.invokeOnCompletion {
        if (System.currentTimeMillis() - lastSync.value!! > 3600000L) {
            startSync()
        } else {
            stopSelf()
        }
    }

    private fun startSync() = shouldUpdate.value?.let {
        when (it) {
            true -> lastSync.value?.let {
                notificationExt.notify("Syncing Mails", "You may have new mails")
                syncWithServer()
                alarmBroadcast.startBroadcast()
                debugLog("Syncing")
                stopSelf()
            }
            else -> {
                alarmBroadcast.stopBroadcast()
                stopSelf()
            }
        }
    }

    private fun syncWithServer() = lastSync.switchMap {
        syncRepository.getMails(
            Constants.INBOX_URL, Constants.UPDATE_QUERY + lastSync.value!!
        ).asLiveData().switchMap {
            MutableLiveData(Event(it))
        }.map { response ->
            response?.let { event ->
                val result = event.peekContent()
                if (result.status == Status.SUCCESS) {
                    result.data?.let { list ->
                        list.body()?.let { sendNotification(it.mails, lastSync.value!!) }
                    }
                    saveLastSync()
                }
            }
        }
    }

    private fun saveLastSync() = CoroutineScope(Dispatchers.IO).launch {
        syncRepository.saveLastSync(
            Constants.KEY_SYNC_SERVICE,
            System.currentTimeMillis() - DateUtils.MINUTE_IN_MILLIS
        )
    }

    private fun sendNotification(list: List<Mail>, lastSync: Long) {
        list.forEach { mail ->
            if (mail.flag.contains('u') && mail.time > lastSync) {
                val sender = mail.addresses.last()
                val name =
                    if (sender.name.isNotEmpty()) sender.name else sender.email.substringBefore(
                        '@'
                    )
                notificationExt.notify("New Mail From $name", mail.subject)
            }
        }
    }
}