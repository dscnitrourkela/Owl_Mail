package github.sachin2dehury.nitrmail.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.nitrmail.others.Constants
import github.sachin2dehury.nitrmail.others.InternetChecker
import github.sachin2dehury.nitrmail.others.debugLog
import github.sachin2dehury.nitrmail.repository.Repository
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class SyncService : Service() {

    @Inject
    lateinit var repository: Repository

    @Inject
    lateinit var internetChecker: InternetChecker

    @Inject
    lateinit var notificationExt: NotificationExt

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        startSyncService()

        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val restartServiceIntent = Intent(applicationContext, this.javaClass)
        restartServiceIntent.setPackage(packageName)
        startService(restartServiceIntent)
        super.onTaskRemoved(rootIntent)
    }

    private fun startSyncService() = GlobalScope.launch {
        val lastSync = repository.readLastSync(Constants.KEY_LAST_SYNC)
        while (true) {
            syncMails(lastSync)
            debugLog("Running Sync")
            delay(10000L)
//            delay(Constants.SYNC_DELAY_TIME)
        }
    }

    private suspend fun syncMails(lastSync: Long) {
        val currentTime = System.currentTimeMillis()
        val response = repository.syncMails(lastSync)
        if (response.isSuccessful && response.code() == 200) {
            response.body()?.let { result ->
                CoroutineScope(Dispatchers.IO).launch {
                    if (internetChecker.isInternetConnected(this@SyncService)) {
                        repository.saveLastSync(Constants.JUNK_URL, currentTime)
                    }
                }
                result.mails.forEach { mail ->
                    notificationExt.notify(mail.senders.last().name, mail.subject)
                }
            }
        }
    }
}