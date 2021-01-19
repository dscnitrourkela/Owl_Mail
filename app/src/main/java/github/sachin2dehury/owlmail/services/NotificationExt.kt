package github.sachin2dehury.owlmail.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.ui.MainActivity

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

    fun notify(title: String, subject: String) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 1000, intent, 0)
        val notification =
            NotificationCompat.Builder(context, Constants.NOTIFICATION_ID).apply {
                priority = NotificationCompat.PRIORITY_DEFAULT
                setStyle(NotificationCompat.BigTextStyle())
                setSmallIcon(R.mipmap.ic_launcher)
                setContentTitle(title)
                setContentText(subject)
                setContentIntent(pendingIntent)
            }
        notificationManager.notify(1000, notification.build())
    }
}