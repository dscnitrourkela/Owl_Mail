package github.sachin2dehury.owlmail.services

import android.annotation.SuppressLint
import android.app.job.JobParameters
import android.app.job.JobService
import android.text.format.DateUtils
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.api.calls.BasicAuthInterceptor
import github.sachin2dehury.owlmail.api.calls.MailApi
import github.sachin2dehury.owlmail.api.data.Mail
import github.sachin2dehury.owlmail.others.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SyncService : JobService() {

    @Inject
    lateinit var basicAuthInterceptor: BasicAuthInterceptor

    @Inject
    lateinit var mailApi: MailApi

    @Inject
    lateinit var notificationExt: NotificationExt

    @SuppressLint("NewApi")
    override fun onStartJob(jobParameters: JobParameters?): Boolean {

        val shouldSync = jobParameters?.extras?.getBoolean(Constants.KEY_SHOULD_SYNC)
        if (shouldSync == false) {
            stopSelf()
        }
        startSyncWorker(jobParameters)
        return true
    }

    override fun onStopJob(jobParameters: JobParameters?) = true

    private fun startSyncWorker(jobParameters: JobParameters?) {
        jobParameters?.let {
            val lastSync = it.extras.getLong(Constants.KEY_SYNC_SERVICE)
            val credential = it.extras.getString(Constants.KEY_CREDENTIAL, Constants.NO_CREDENTIAL)
            val token = it.extras.getString(Constants.KEY_TOKEN, Constants.NO_TOKEN)
            basicAuthInterceptor.token = token
            basicAuthInterceptor.credential = credential
            notificationExt.notify("Syncing Mails", "You may have some new mails")
            fetchMails(lastSync).invokeOnCompletion { throwable ->
                notificationExt.cancelNotify()
                when (throwable) {
                    null -> jobFinished(jobParameters, false)
                    else -> jobFinished(jobParameters, true)
                }
            }
        }
    }

    private fun fetchMails(lastSync: Long) = CoroutineScope(Dispatchers.IO).launch {
        val list = mailApi.getMails(
            applicationContext.getString(R.string.inbox),
            Constants.AFTER_QUERY + (lastSync - DateUtils.HOUR_IN_MILLIS)
        ).body()?.mails
        list?.let { sendNotification(it, lastSync) }
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

            val sender = mail.addresses.last()
            notificationExt.notify(
                "New Mail From ${sender.firstName}",
                mail.subject ?: "",
                mail.id
            )
        }
    }
}