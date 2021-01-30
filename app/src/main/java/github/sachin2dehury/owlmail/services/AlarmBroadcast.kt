package github.sachin2dehury.owlmail.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.text.format.DateUtils

class AlarmBroadcast(private val context: Context) {

    private val currentTime = System.currentTimeMillis()
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val alarmIntent = Intent(context, SyncBroadcastReceiver::class.java).let { intent ->
        PendingIntent.getBroadcast(context, 0, intent, 0)
    }

//    val calendar = Calendar.getInstance().apply {
//        timeInMillis = currentTime
//        set(Calendar.HOUR_OF_DAY, 8)
//    }

    fun startBroadcast() = alarmManager.setInexactRepeating(
        AlarmManager.RTC_WAKEUP,
        currentTime + DateUtils.MINUTE_IN_MILLIS,
        DateUtils.MINUTE_IN_MILLIS,
        alarmIntent
    )

    fun stopBroadcast() = alarmManager.cancel(alarmIntent)
}