package github.sachin2dehury.nitrmail.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import github.sachin2dehury.nitrmail.others.debugLog
import javax.inject.Inject

class CloudMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationExt: NotificationExt

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        debugLog("${message.notification}")
        message.notification.let {
            notificationExt.notify(it?.title.toString(), it?.body.toString())
        }
    }

    override fun onNewToken(token: String) {
        debugLog("CloudMessages onNewToken : $token")
        super.onNewToken(token)
    }
}