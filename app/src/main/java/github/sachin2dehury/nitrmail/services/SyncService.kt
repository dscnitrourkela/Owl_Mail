package github.sachin2dehury.nitrmail.services

import android.content.Intent
import android.text.format.DateUtils
import androidx.lifecycle.*
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.nitrmail.api.data.Mail
import github.sachin2dehury.nitrmail.others.*
import github.sachin2dehury.nitrmail.repository.Repository
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SyncService : LifecycleService() {

    init {
        debugLog("SyncService Started")
    }

    @Inject
    lateinit var repository: Repository

    @Inject
    lateinit var notificationExt: NotificationExt

    @Inject
    lateinit var alarmBroadcast: AlarmBroadcast

    private val _lastSync = MutableLiveData(System.currentTimeMillis())

    private val _mails = MutableLiveData<Event<Resource<List<Mail>>>>()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        startSyncService()

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val restartServiceIntent = Intent(applicationContext, this.javaClass)
        restartServiceIntent.setPackage(packageName)
        startService(restartServiceIntent)
        super.onTaskRemoved(rootIntent)
    }

    private fun startSyncService() = lifecycleScope.launch {
        debugLog("startSyncService : ${_lastSync.value}")
//        syncMails()
        notificationExt.notify("Test ${System.currentTimeMillis()}", "Hello boy")
        alarmBroadcast.startBroadcast()
    }

    private suspend fun syncMails() {
        _lastSync.postValue(repository.readLastSync(Constants.INBOX_URL))
        _mails.postValue(
            repository.getMails(
                Constants.INBOX_URL, Constants.UPDATE_QUERY + _lastSync.value!!
            ).asLiveData(lifecycleScope.coroutineContext).switchMap {
                MutableLiveData(Event(it))
            }.value
        )
        _mails.value?.let { event ->
            val result = event.peekContent()
            if (result.status == Status.SUCCESS) {
                repository.saveLastSync(
                    Constants.INBOX_URL,
                    System.currentTimeMillis() - DateUtils.MINUTE_IN_MILLIS
                )
                result.data?.let { list ->
                    list.forEach { mail ->
                        if (mail.flag.contains('u')) {
                            val sender = mail.addresses.last()
                            val name =
                                if (sender.name.isNotEmpty()) sender.name else sender.email.substringBefore(
                                    '@'
                                )
                            notificationExt.notify("New Mail From $name", mail.subject)
                        }
                    }
                }
            }
        }
    }
}