package github.sachin2dehury.owlmail.services

import android.annotation.SuppressLint
import android.app.job.JobParameters
import android.app.job.JobService
import android.text.format.DateUtils
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.owlmail.api.data.Mail
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.others.Resource
import github.sachin2dehury.owlmail.others.debugLog
import github.sachin2dehury.owlmail.repository.DataStoreRepository
import github.sachin2dehury.owlmail.repository.MailRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SyncService : JobService() {

    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    @Inject
    lateinit var mailRepository: MailRepository

    @Inject
    lateinit var notificationExt: NotificationExt

    private var shouldSync = false
    private var lastSync = System.currentTimeMillis()
    private val mails = MutableLiveData<Resource<List<Mail>>>()

    @SuppressLint("NewApi")
    override fun onStartJob(jobParameters: JobParameters?): Boolean {

        jobParameters?.let {
            shouldSync = it.extras.getBoolean(Constants.KEY_SHOULD_SYNC)
            if (!shouldSync) {
                stopSelf()
            }
            lastSync = it.extras.getLong(Constants.KEY_SYNC_SERVICE)
            val credential = it.extras.getString(Constants.KEY_CREDENTIAL, Constants.NO_CREDENTIAL)
            val token = it.extras.getString(Constants.KEY_TOKEN, Constants.NO_TOKEN)
            mailRepository.setToken(token)
            mailRepository.setCredential(credential)
        }
        startSyncWorker(jobParameters)
        return true
    }

    override fun onStopJob(jobParameters: JobParameters?) = true

    private fun startSyncWorker(jobParameters: JobParameters?) {
        debugLog("SyncService startSyncWorker")
        notificationExt.notify("Syncing Mails", "You may have some new mails")
        notificationExt.cancelNotify()
        fetchMails().invokeOnCompletion {
            when (it) {
                null -> {
                    mails.value?.data?.let { list -> sendNotification(list, lastSync) }
                    saveLastSync()
                    jobFinished(jobParameters, false)
                }
                else -> jobFinished(jobParameters, true)
            }
            debugLog("SyncService startSyncWorker ${mails.value?.data}")
        }
    }

    private fun fetchMails() = CoroutineScope(Dispatchers.IO).launch {
        mails.postValue(
            mailRepository.getMails(Constants.INBOX_URL, Constants.UPDATE_QUERY + lastSync).first()
        )
        delay(5000)
    }

    private fun sendNotification(list: List<Mail>, lastSync: Long) {
        list.forEach { mail ->
            if (mail.flag.contains('u') && mail.time > lastSync) {
                val id = mail.id.toInt()
                val sender = mail.addresses.last()
                val name =
                    if (sender.name.isNotEmpty()) sender.name else sender.email.substringBefore('@')
                notificationExt.notify("New Mail From $name", mail.subject, id)
            }
        }
    }

    private fun saveLastSync() = CoroutineScope(Dispatchers.IO).launch {
        dataStoreRepository.saveLastSync(
            Constants.KEY_SYNC_SERVICE,
            System.currentTimeMillis() - DateUtils.MINUTE_IN_MILLIS
        )
    }
}