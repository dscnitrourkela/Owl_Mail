package github.sachin2dehury.nitrmail.services

import android.content.Context
import androidx.lifecycle.asLiveData
import androidx.work.Worker
import androidx.work.WorkerParameters
import github.sachin2dehury.nitrmail.others.Constants
import github.sachin2dehury.nitrmail.others.InternetChecker
import github.sachin2dehury.nitrmail.others.Status
import github.sachin2dehury.nitrmail.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class SyncWorkManager(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    @Inject
    lateinit var repository: Repository

    @Inject
    lateinit var internetChecker: InternetChecker

    @Inject
    lateinit var notificationExt: NotificationExt

    private fun syncMails(lastSync: Long): Result {
        val currentTime = System.currentTimeMillis()
        val response = repository.getMails(Constants.INBOX_URL, lastSync)
            .asLiveData(GlobalScope.coroutineContext).value

        response?.let { result ->
            if (result.status == Status.SUCCESS) {
                CoroutineScope(Dispatchers.IO).launch {
                    repository.saveLastSync(Constants.INBOX_URL, currentTime)
                }
                if (internetChecker.isInternetConnected(applicationContext)) {
                    result.data?.let { mails ->
                        mails.forEach { mail ->
                            notificationExt.notify(mail.senders.last().name, mail.subject)
                        }
                    }
                }
                return Result.success()
            }
        }
        return Result.failure()
    }

    override fun doWork(): Result {
        var result = Result.failure()
        var lastSync = Constants.NO_LAST_SYNC
        CoroutineScope(Dispatchers.IO).launch {
            lastSync = repository.readLastSync(Constants.KEY_LAST_SYNC)
        }.invokeOnCompletion {
            result = syncMails(lastSync)
        }
        return result
    }
}