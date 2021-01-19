package github.sachin2dehury.owlmail.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import github.sachin2dehury.owlmail.others.debugLog


class AlarmBroadcast(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val alarmIntent = Intent(context, SyncBroadcastReceiver::class.java).let { intent ->
        PendingIntent.getBroadcast(context, 0, intent, 0)
    }

    fun startBroadcast() {
        val currentTime = System.currentTimeMillis()
//        val calendar = Calendar.getInstance().apply {
//            timeInMillis = currentTime
//            set(Calendar.HOUR_OF_DAY, 8)
//        }

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            currentTime,
            AlarmManager.INTERVAL_HOUR,
            alarmIntent
        )
        debugLog("broadcastSync AlarmBroadcast $currentTime")
    }

    fun stopBroadcast() {
        alarmManager.cancel(alarmIntent)
    }
}