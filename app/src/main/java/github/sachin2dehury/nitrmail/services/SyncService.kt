package github.sachin2dehury.nitrmail.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.nitrmail.R
import github.sachin2dehury.nitrmail.api.calls.MailApi
import github.sachin2dehury.nitrmail.others.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SyncService : Service() {

    @Inject
    lateinit var mailApi: MailApi

    private var lastSync = Constants.NO_LAST_SYNC

    private lateinit var notificationManager: NotificationManagerCompat

    private fun setupNotificationManager(): NotificationManagerCompat {
        notificationManager = NotificationManagerCompat.from(applicationContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                Constants.NOTIFICATION_TAG,
                Constants.NOTIFICATION_CHANNEL,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.WHITE
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        return notificationManager
    }

    private fun makeNotification(notify: String, info: String) {
        val notification = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL).apply {
            priority = NotificationCompat.PRIORITY_HIGH
            setStyle(NotificationCompat.InboxStyle(this))
            setSmallIcon(R.mipmap.ic_launcher)
            setContentTitle("New Mail From $notify")
            setContentInfo(info)
        }
        notificationManager.notify(1000, notification.build())
//        startForeground(1000, notification)
    }

    private fun makeNetworkCalls() = CoroutineScope(Dispatchers.IO).launch {
//        while (true) {
//            getNewMails()
//            delay(10000L)
//
//        }
        getNewMails()
    }

    private suspend fun getNewMails() {
        val result = mailApi.getMails(Constants.JUNK_URL, Constants.UPDATE_QUERY + lastSync)
        result.body()?.mails?.let { list ->
            if (list.isNotEmpty()) {
                list.forEach { mail ->
                    makeNotification(mail.senders.first().name, mail.subject)
                }
            }
        }
        lastSync = System.currentTimeMillis()
    }

    override fun onCreate() {
        super.onCreate()
        setupNotificationManager()
//        makeNetworkCalls()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        lastSync = intent?.getLongExtra(Constants.KEY_LAST_SYNC, Constants.NO_LAST_SYNC)
            ?: Constants.NO_LAST_SYNC

        makeNotification("Test", "Test")

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        val restartServiceIntent = Intent(applicationContext, this.javaClass)
        restartServiceIntent.setPackage(packageName)
        startService(restartServiceIntent)
        super.onTaskRemoved(rootIntent)
    }

    override fun onBind(p0: Intent?): IBinder? = null
}