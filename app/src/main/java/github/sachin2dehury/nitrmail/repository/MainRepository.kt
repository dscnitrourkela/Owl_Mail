package github.sachin2dehury.nitrmail.repository

import android.app.Application
import github.sachin2dehury.nitrmail.api.calls.MailApi
import github.sachin2dehury.nitrmail.api.data.Mail
import github.sachin2dehury.nitrmail.api.database.MailDao
import github.sachin2dehury.nitrmail.others.Resource
import github.sachin2dehury.nitrmail.others.isInternetConnected
import github.sachin2dehury.nitrmail.others.networkBoundResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val mailApi: MailApi,
    private val mailDao: MailDao,
    private val context: Application
) {

    private suspend fun insertMails(mails: List<Mail>, request: String) {
        mails.forEach {
            it.box = request
            mailDao.insertMail(it)
        }
    }

    fun getMails(request: String): Flow<Resource<List<Mail>>> {
        return networkBoundResource(
            query = {
                mailDao.getAllMails(request)
            },
            fetch = {
                mailApi.getMails(request)
            },
            saveFetchResult = { response ->
                response.body()?.let {
                    insertMails(it.mails, request)
                }
            },
            shouldFetch = {
                isInternetConnected(context)
            }
        )
    }

    suspend fun login() = withContext(Dispatchers.IO) {
        try {
            val response = mailApi.getMails()
            if (response.isSuccessful && response.code() == 200) {
                Resource.success(response.body()?.mails)
            } else {
                Resource.error(response.message() ?: response.message(), null)
            }
        } catch (e: Exception) {
            Resource.error("Couldn't connect to the servers. Check your internet connection", null)
        }
    }
}