package github.sachin2dehury.nitrmail.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.nitrmail.others.debugLog

@AndroidEntryPoint
class SyncBroadcastReceiver : BroadcastReceiver() {

    init {
        debugLog("Created SyncBroadcastReceiver")
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val syncIntent = Intent(context, SyncService::class.java)
            context.startService(syncIntent)
        }
        debugLog("Running SyncBroadcastReceiver")
    }
}