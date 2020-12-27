package github.sachin2dehury.nitrmail.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.nitrmail.R
import github.sachin2dehury.nitrmail.others.Constants
import github.sachin2dehury.nitrmail.repository.MainRepository
import javax.inject.Inject

@AndroidEntryPoint
class SyncService : Service() {

    init {
        Log.w("Test", "Service")
    }

    @Inject
    lateinit var repository: MainRepository

    var lastSync = Constants.NO_LAST_SYNC

    private lateinit var notificationManager: NotificationManagerCompat

    private fun setupNotificationManager(): NotificationManagerCompat {
        notificationManager = NotificationManagerCompat.from(this)
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

    fun makeNotification(notify: String) {
        val notification = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL).apply {
            priority = NotificationCompat.PRIORITY_DEFAULT
            setSmallIcon(R.mipmap.ic_launcher)
            setContentTitle(notify)
        }
        notificationManager.notify(1000, notification.build())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val lastSync = intent?.getLongExtra(Constants.KEY_LAST_SYNC, Constants.NO_LAST_SYNC)
            ?: Constants.NO_LAST_SYNC

        setupNotificationManager()

        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        val restartServiceIntent = Intent(applicationContext, this.javaClass)
        restartServiceIntent.setPackage(packageName)
        startService(restartServiceIntent)
        super.onTaskRemoved(rootIntent)
    }

    override fun onBind(p0: Intent?): IBinder? = null
}