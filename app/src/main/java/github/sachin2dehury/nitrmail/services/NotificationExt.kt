package github.sachin2dehury.nitrmail.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import github.sachin2dehury.nitrmail.R
import github.sachin2dehury.nitrmail.others.Constants

class NotificationExt(private val context: Context) {

    private val notificationManager = NotificationManagerCompat.from(context).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                Constants.NOTIFICATION_ID,
                Constants.NOTIFICATION_CHANNEL,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.apply {
                enableLights(true)
                lightColor = Color.WHITE
                enableVibration(true)
            }
            createNotificationChannel(notificationChannel)
        }
    }

    fun notify(name: String, subject: String) {
        val notification =
            NotificationCompat.Builder(context, Constants.NOTIFICATION_ID).apply {
                priority = NotificationCompat.PRIORITY_HIGH
                setStyle(NotificationCompat.InboxStyle(this))
                setSmallIcon(R.mipmap.ic_launcher)
                setContentTitle("New Mail From $name")
                setContentInfo(subject)
            }
        notificationManager.notify(1000, notification.build())
    }
}