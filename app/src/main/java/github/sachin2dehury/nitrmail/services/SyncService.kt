package github.sachin2dehury.nitrmail.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.nitrmail.api.data.Mail
import github.sachin2dehury.nitrmail.others.Constants
import github.sachin2dehury.nitrmail.others.Event
import github.sachin2dehury.nitrmail.others.Resource
import github.sachin2dehury.nitrmail.others.debugLog
import github.sachin2dehury.nitrmail.repository.Repository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SyncService : Service() {

    @Inject
    lateinit var repository: Repository

    @Inject
    lateinit var notificationExt: NotificationExt

    private val _currentTime = MutableLiveData(System.currentTimeMillis())

    private val _lastSync = MutableLiveData(_currentTime.value)

    private val _forceUpdate = MutableLiveData(false)

    private val _mails = MutableLiveData<Event<Resource<List<Mail>>>>()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        startSyncService()

        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val restartServiceIntent = Intent(applicationContext, this.javaClass)
        restartServiceIntent.setPackage(packageName)
        startService(restartServiceIntent)
        super.onTaskRemoved(rootIntent)
    }

    private fun startSyncService() = GlobalScope.launch {
        debugLog("startSyncService : ${_lastSync.value}")
        while (true) {
            syncMails()
            delay(Constants.SYNC_DELAY_TIME)
        }
    }

    private suspend fun syncMails() {
//        _currentTime.postValue(System.currentTimeMillis())
//        _mails.postValue(
//            repository.getMails(
//                Constants.INBOX_URL,
//                Constants.UPDATE_QUERY + _lastSync.value!!
//            )
//                .asLiveData(GlobalScope.coroutineContext).switchMap {
//                    MutableLiveData(Event(it))
//                }.value
//        )
//        if (response.isSuccessful && response.code() == 200) {
//            response.body()?.let { result ->
//                lastSync = currentTime
//                result.mails.forEach { mail ->
//                    if (mail.flag.contains('u')) {
//                        val sender = mail.addresses.last()
//                        val name =
//                            if (sender.name.isNotEmpty()) sender.name else sender.email.substringBefore(
//                                '@'
//                            )
//                        notificationExt.notify(name, mail.subject)
//                    }
//                }
//            }
//        }
    }
}