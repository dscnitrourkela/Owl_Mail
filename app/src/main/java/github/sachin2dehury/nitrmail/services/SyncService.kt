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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.nitrmail.R
import github.sachin2dehury.nitrmail.others.Constants
import github.sachin2dehury.nitrmail.others.Event
import github.sachin2dehury.nitrmail.others.InternetChecker
import github.sachin2dehury.nitrmail.others.Status
import github.sachin2dehury.nitrmail.repository.Repository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SyncService @Inject constructor(
    private val repository: Repository,
    private val internetChecker: InternetChecker
) : Service() {

    var lastSync = Constants.NO_LAST_SYNC

    private lateinit var notificationManager: NotificationManagerCompat

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        lastSync = intent?.getLongExtra(Constants.KEY_LAST_SYNC, Constants.NO_LAST_SYNC)
            ?: Constants.NO_LAST_SYNC

        setupNotificationManager()
        makeNetworkCalls()

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        val restartServiceIntent = Intent(applicationContext, this.javaClass)
        restartServiceIntent.setPackage(packageName)
        startService(restartServiceIntent)
        super.onTaskRemoved(rootIntent)
    }

    override fun onBind(p0: Intent?): IBinder? = null

    private fun setupNotificationManager(): NotificationManagerCompat {
        notificationManager = NotificationManagerCompat.from(applicationContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                Constants.NOTIFICATION_ID,
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
        val notification =
            NotificationCompat.Builder(applicationContext, Constants.NOTIFICATION_ID).apply {
                priority = NotificationCompat.PRIORITY_HIGH
                setStyle(NotificationCompat.InboxStyle(this))
                setSmallIcon(R.mipmap.ic_launcher)
                setContentTitle("New Mail From $notify")
                setContentInfo(info)
            }
        notificationManager.notify(1000, notification.build())
    }

    private fun makeNetworkCalls(time: Long = 3600000L) = GlobalScope.launch {
        repository.saveLastSync(Constants.INBOX_URL, lastSync)
        getNewMails()
        delay(time)
    }

    private fun getNewMails() {
        val currentTime = System.currentTimeMillis()
        val response = repository.getMails(Constants.INBOX_URL, lastSync)
            .asLiveData(GlobalScope.coroutineContext).switchMap {
                MutableLiveData(Event(it))
            }
        response.observeForever {
            it?.let { event ->
                val result = event.peekContent()
                when (result.status) {
                    Status.SUCCESS -> {
                        if (internetChecker.isInternetConnected(applicationContext)) {
                            lastSync = currentTime
                            result.data?.let { mails ->
                                mails.forEach { mail ->
                                    makeNotification(mail.senders.last().name, mail.subject)
                                }
                            }
                        }
                    }
                    Status.ERROR -> {
                        makeNetworkCalls(600000L)
                    }
                    Status.LOADING -> {
                    }
                }
            }
        }
    }
}