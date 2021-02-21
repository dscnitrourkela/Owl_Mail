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
            debugLog(credential + token + lastSync + shouldSync)
        }
        startSyncWorker(jobParameters)
        return true
    }

    override fun onStopJob(jobParameters: JobParameters?) = true

    private fun startSyncWorker(jobParameters: JobParameters?) {
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
        }
    }

    private fun fetchMails() = CoroutineScope(Dispatchers.IO).launch {
//        mails.postValue(
//            mailRepository.getMails(applicationContext.getString(R.string.inbox), lastSync).first()
//        )
        debugLog(mails.value?.data.toString())
        delay(5000)
        debugLog(mails.value?.data.toString())
    }

    private fun sendNotification(list: List<Mail>, lastSync: Long) {
        list.forEach { mail ->
            if (mail.flag?.contains('u') == true && mail.time > lastSync) {
                val sender = mail.addresses.last()
                notificationExt.notify(
                    "New Mail From ${sender.firstName}",
                    mail.subject ?: "",
                    mail.id
                )
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