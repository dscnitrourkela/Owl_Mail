package github.sachin2dehury.nitrmail.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import github.sachin2dehury.nitrmail.others.debugLog

class SyncBroadcastReceiver : BroadcastReceiver() {

    init {
        debugLog("Created SyncBroadcastReceiver")
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val syncIntent = Intent(context, SyncService::class.java)
        when (intent?.action) {
            Intent.ACTION_SCREEN_ON -> context?.startService(syncIntent)
            Intent.ACTION_SCREEN_OFF -> context?.stopService(syncIntent)
        }
        debugLog("Running SyncBroadcastReceiver")
    }
}