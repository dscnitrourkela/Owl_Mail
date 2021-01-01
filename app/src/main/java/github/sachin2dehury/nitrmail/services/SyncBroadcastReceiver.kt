package github.sachin2dehury.nitrmail.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class SyncBroadcastReceiver : BroadcastReceiver() {

    init {
        Log.w("Test", "Created")
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val syncIntent = Intent(context, SyncService::class.java)
        when (intent?.action) {
            Intent.ACTION_SCREEN_ON -> context?.startService(syncIntent)
            Intent.ACTION_SCREEN_OFF -> context?.stopService(syncIntent)
        }
        Log.w("Test", "Running")
    }
}