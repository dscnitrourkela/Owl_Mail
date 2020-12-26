package github.sachin2dehury.nitrmail.repository

import android.app.Application
import github.sachin2dehury.nitrmail.api.calls.MailApi
import github.sachin2dehury.nitrmail.api.data.mails.Mail
import github.sachin2dehury.nitrmail.api.databases.mails.MailDao
import github.sachin2dehury.nitrmail.others.Constants
import github.sachin2dehury.nitrmail.others.Resource
import github.sachin2dehury.nitrmail.others.isInternetConnected
import github.sachin2dehury.nitrmail.others.networkBoundResource
import github.sachin2dehury.nitrmail.parser.data.ParsedMail
import github.sachin2dehury.nitrmail.parser.parsedmails.ParsedMailDao
import github.sachin2dehury.nitrmail.parser.util.MailParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val mailApi: MailApi,
    private val mailDao: MailDao,
    private val parsedMailDao: ParsedMailDao,
    private val context: Application
) {

    private suspend fun insertMails(mails: List<Mail>, request: String) {
        mails.forEach {
            it.box = request
            mailDao.insertMail(it)
        }
    }

    private suspend fun insertParsedMail(parsedMail: ParsedMail) {
        parsedMailDao.insertMail(parsedMail)
    }

    private suspend fun getRawMailItem(id: String): ParsedMail = withContext(Dispatchers.IO) {
        val result = mailApi.getMailItem(id).string().byteInputStream()
        val parsedMail = MailParser().parse(result)
        parsedMail.id = id
        return@withContext parsedMail
    }

    fun getParsedMailItem(
        id: String
    ): Flow<Resource<ParsedMail>> {
        return networkBoundResource(
            query = {
                parsedMailDao.getMailItem(id)
            },
            fetch = {
                getRawMailItem(id)
            },
            saveFetchResult = {
                insertParsedMail(it)
            },
            shouldFetch = {
                isInternetConnected(context)
            },
        )
    }

    fun getMails(request: String, lastSync: Long): Flow<Resource<List<Mail>>> {
        return networkBoundResource(
            query = {
                mailDao.getMails(request)
            },
            fetch = {
                mailApi.getMails(request, Constants.UPDATE_QUERY + lastSync)
            },
            saveFetchResult = { response ->
                response.body()?.let {
                    insertMails(it.mails, request)
                }
            },
            shouldFetch = {
                isInternetConnected(context)
            },
        )
    }

    suspend fun login() = withContext(Dispatchers.IO) {
        try {
            val response =
                mailApi.getMails(
                    Constants.INBOX_URL,
                    Constants.UPDATE_QUERY + System.currentTimeMillis()
                )
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