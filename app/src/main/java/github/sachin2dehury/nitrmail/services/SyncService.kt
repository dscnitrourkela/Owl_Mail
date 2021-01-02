package github.sachin2dehury.nitrmail.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.asLiveData
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.nitrmail.others.Constants
import github.sachin2dehury.nitrmail.others.InternetChecker
import github.sachin2dehury.nitrmail.others.Status
import github.sachin2dehury.nitrmail.repository.Repository
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class SyncService : Service() {

    init {
        Log.w("Test", "Created SyncService")
    }

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
            notificationExt.notify("Test", "Test")
//            syncMails(lastSync)
            Log.w("Test", "Sync Mail")
//            delay(Constants.SYNC_DELAY_TIME)
            delay(10000L)
        }
    }

    private fun syncMails(lastSync: Long) {
        val currentTime = System.currentTimeMillis()
        val response = repository.getMails(Constants.JUNK_URL, Constants.UPDATE_QUERY + lastSync)
            .asLiveData(GlobalScope.coroutineContext).value

        response?.let { result ->
            if (result.status == Status.SUCCESS) {
                CoroutineScope(Dispatchers.IO).launch {
                    repository.saveLastSync(Constants.JUNK_URL, currentTime)
                }
                if (internetChecker.isInternetConnected(applicationContext)) {
                    result.data?.let { mails ->
                        mails.forEach { mail ->
                            notificationExt.notify(mail.senders.last().name, mail.subject)
                        }
                    }
                }
            }
        }
    }
}