package github.sachin2dehury.nitrmail.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.nitrmail.others.Constants
import github.sachin2dehury.nitrmail.others.InternetChecker
import github.sachin2dehury.nitrmail.repository.Repository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SyncService : Service() {

    @Inject
    lateinit var repository: Repository

    @Inject
    lateinit var internetChecker: InternetChecker

    @Inject
    lateinit var notificationExt: NotificationExt

    private var lastSync = System.currentTimeMillis()

    override fun onBind(intent: Intent?): IBinder? = null

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
        lastSync = repository.readLastSync(Constants.KEY_LAST_SYNC)
        while (true) {
            syncMails()
            delay(Constants.SYNC_DELAY_TIME)
        }
    }

    private suspend fun syncMails() {
        val currentTime = System.currentTimeMillis() - 1000
        val response = repository.syncMails(lastSync)
        if (response.isSuccessful && response.code() == 200) {
            response.body()?.let { result ->
                lastSync = currentTime
                result.mails.forEach { mail ->
                    if (mail.flag.contains('u')) {
                        val sender = mail.addresses.last()
                        val name =
                            if (sender.name.isNotEmpty()) sender.name else sender.email.substringBefore(
                                '@'
                            )
                        notificationExt.notify(name, mail.subject)
                    }
                }
            }
        }
    }
}