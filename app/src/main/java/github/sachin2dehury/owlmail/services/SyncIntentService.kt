package github.sachin2dehury.owlmail.services

import android.content.Intent
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.map
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.owlmail.api.data.Mail
import github.sachin2dehury.owlmail.others.debugLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SyncIntentService : LifecycleService() {

    @Inject
    lateinit var syncWorker: SyncWorker

    override fun onCreate() {
        super.onCreate()
        syncWorker.refresh()
        debugLog("SyncIntentService onCreate")
        debugLog("${syncWorker.isLoggedIn.value}")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startSyncWorker()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startSyncWorker() = CoroutineScope(lifecycleScope.coroutineContext).launch {
        debugLog("SyncIntentService startSyncWorker")
        syncWorker.notificationExt.notify("Syncing Mails", "You may have some new mails")
        delay(10000L)
        debugLog("SyncIntentService startSyncWorker after 10secs")
        debugLog("${syncWorker.isLoggedIn.value}")
        if (syncWorker.isLoggedIn.value == true && syncWorker.shouldUpdate.value == true) {
            syncWorker.notificationExt.notify("Syncing Mails", "You may have some new mails")
            syncWorker.mails.map {
                it.peekContent().data?.let { list ->
                    sendNotification(list, syncWorker.lastSync.value!!)
                }
            }
            syncWorker.alarmBroadcast.startBroadcast()
        } else {
            syncWorker.alarmBroadcast.stopBroadcast()
        }
        stopSelf()
        debugLog("SyncIntentService stopSelf")
    }

    private fun sendNotification(list: List<Mail>, lastSync: Long) {
        list.forEach { mail ->
            if (mail.flag.contains('u') && mail.time > lastSync) {
                val id = mail.id.toInt()
                val sender = mail.addresses.last()
                val name =
                    if (sender.name.isNotEmpty()) sender.name else sender.email.substringBefore('@')
                syncWorker.notificationExt.notify("New Mail From $name", mail.subject, id)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        debugLog("SyncIntentService onDestroy")
    }
}