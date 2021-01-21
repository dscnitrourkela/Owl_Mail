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
import github.sachin2dehury.owlmail.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SyncService : LifecycleService() {

    @Inject
    lateinit var repository: Repository

    @Inject
    lateinit var notificationExt: NotificationExt

    @Inject
    lateinit var alarmBroadcast: AlarmBroadcast

    private val shouldUpdate =
        repository.readState(Constants.KEY_SHOULD_SYNC).asLiveData(lifecycleScope.coroutineContext)
            .map {
                it ?: false
            }

    private fun saveLastSync() = CoroutineScope(Dispatchers.IO).launch {
        repository.saveLastSync(
            Constants.KEY_SYNC_SERVICE,
            System.currentTimeMillis() - DateUtils.MINUTE_IN_MILLIS
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when (shouldUpdate.value) {
            true -> {
                notificationExt.notify("Syncing Mails", "")
                startSyncService()
                alarmBroadcast.startBroadcast()
            }
            else -> stopSelf()
        }
        debugLog("onStartCommand Finished")

        return super.onStartCommand(intent, flags, startId)
    }

//    override fun onTaskRemoved(rootIntent: Intent?) {
//        debugLog("onTaskRemoved")
//        val restartServiceIntent = Intent(applicationContext, this.javaClass)
//        restartServiceIntent.setPackage(packageName)
//        startService(restartServiceIntent)
//        super.onTaskRemoved(rootIntent)
//    }

    private fun startSyncService() {
        val lastSync = repository.readLastSync(Constants.KEY_SYNC_SERVICE)
            .asLiveData(lifecycleScope.coroutineContext).map { it ?: System.currentTimeMillis() }

        val mails = shouldUpdate.switchMap {
            repository.getMails(
                Constants.INBOX_URL, Constants.UPDATE_QUERY + lastSync.value!!
            ).asLiveData(lifecycleScope.coroutineContext).switchMap {
                MutableLiveData(Event(it))
            }
        }
        debugLog("startSyncService : ${lastSync.value}")
        mails.value?.let { event ->
            val result = event.peekContent()
            if (result.status == Status.SUCCESS) {
                saveLastSync()
                result.data?.let { list ->
                    sendNotification(list, lastSync.value!!)
                }
            }
        }
        debugLog("startSyncService Ended")
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

    override fun stopService(name: Intent?): Boolean {
        debugLog("Stopping Sync Service")
        alarmBroadcast.stopBroadcast()
        return super.stopService(name)
    }
}