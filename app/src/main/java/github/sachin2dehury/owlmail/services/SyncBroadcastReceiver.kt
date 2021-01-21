package github.sachin2dehury.owlmail.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.owlmail.others.debugLog

@AndroidEntryPoint
class SyncBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.intent.action.BOOT_COMPLETED") {
            debugLog("Running SyncBroadcastReceiver on BOOT_COMPLETED")
        }
        context?.let {
            val syncIntent = Intent(context, SyncService::class.java)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                context.startForegroundService(syncIntent)
//            } else {
//                context.startService(syncIntent)
//            }
            context.startService(syncIntent)
        }
        debugLog("Running SyncBroadcastReceiver")
    }
}