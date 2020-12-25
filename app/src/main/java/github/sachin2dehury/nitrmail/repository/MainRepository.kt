package github.sachin2dehury.nitrmail.repository

import android.app.Application
import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import github.sachin2dehury.nitrmail.api.calls.MailApi
import github.sachin2dehury.nitrmail.api.calls.ParseMailApi
import github.sachin2dehury.nitrmail.api.data.mails.Mail
import github.sachin2dehury.nitrmail.api.data.parsedmails.EncodedMail
import github.sachin2dehury.nitrmail.api.data.parsedmails.ParsedMail
import github.sachin2dehury.nitrmail.api.databases.mails.MailDao
import github.sachin2dehury.nitrmail.api.databases.parsedmails.ParsedMailDao
import github.sachin2dehury.nitrmail.others.Constants
import github.sachin2dehury.nitrmail.others.Resource
import github.sachin2dehury.nitrmail.others.isInternetConnected
import github.sachin2dehury.nitrmail.others.networkBoundResource
import github.sachin2dehury.nitrmail.parser.util.MailParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val mailApi: MailApi,
    private val parseMailApi: ParseMailApi,
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

    suspend fun getRawMailItem(id: String): String = withContext(Dispatchers.IO) {
        val result = mailApi.getMailItem(id).string()
        val local = MailParser().parse(result.byteInputStream())
        Log.w("Testing", "$local")
        return@withContext Base64.encodeToString(result.encodeToByteArray(), Base64.DEFAULT)
    }

    fun getParsedMailItem(request: String, id: String): Flow<Resource<ParsedMail>> {
        val encodedRequest = Gson().toJson(EncodedMail(request))
            .toRequestBody("application/json".toMediaTypeOrNull())
        return networkBoundResource(
            query = {
                parsedMailDao.getMailItem(id)
            },
            fetch = {
                parseMailApi.getParsedMail(encodedRequest)
            },
            saveFetchResult = { response ->
                response.body()?.let { parsedMail ->
                    parsedMail.id = id
                    parsedMailDao.insertMail(parsedMail)
                }
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