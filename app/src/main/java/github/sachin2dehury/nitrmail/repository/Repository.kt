package github.sachin2dehury.nitrmail.repository

import android.app.Application
import github.sachin2dehury.nitrmail.api.calls.BasicAuthInterceptor
import github.sachin2dehury.nitrmail.api.calls.MailApi
import github.sachin2dehury.nitrmail.api.data.Mail
import github.sachin2dehury.nitrmail.api.database.MailDao
import github.sachin2dehury.nitrmail.others.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import javax.inject.Inject

class Repository @Inject constructor(
    private val basicAuthInterceptor: BasicAuthInterceptor,
    private val context: Application,
    private val dataStore: DataStoreExt,
    private val internetChecker: InternetChecker,
    private val mailApi: MailApi,
    private val mailDao: MailDao,
    private val networkBoundResource: NetworkBoundResource,
) {

    fun getParsedMailItem(
        id: String,
        hasAttachments: Boolean
    ): Flow<Resource<Mail>> {
        return networkBoundResource.makeNetworkRequest(
            query = {
                mailDao.getMailItem(id)
            },
            fetch = {
                mailApi.getMailItemBody(Constants.I_MESSAGE_URL, id)
            },
            saveFetchResult = { result ->
                val token = getToken().substringAfter('=')
                val attachments = getAttachments(id)
                var body = result.string()
                if (hasAttachments) {
                    body = "$body<br><br>$attachments"
                }
                body.replace("auth=co", "auth=qp&zauthtoken=$token")
                mailDao.updateMail(body, id)
            },
            shouldFetch = {
                internetChecker.isInternetConnected(context)
            },
        )
    }

    fun getMails(request: String, search: String): Flow<Resource<List<Mail>>> {
        val box = getBox(request)
        return networkBoundResource.makeNetworkRequest(
            query = {
                mailDao.getMails(box)
            },
            fetch = {
                mailApi.getMails(request, search)
            },
            saveFetchResult = { response ->
                response.body()?.mails?.let { mails ->
                    mails.forEach {
                        mailDao.insertMail(it)
                    }
                }
            },
            shouldFetch = {
                internetChecker.isInternetConnected(context)
            },
        )
    }

    suspend fun login(credential: String) = withContext(Dispatchers.IO) {
        basicAuthInterceptor.credential = credential
        try {
            val response =
                mailApi.login(Constants.UPDATE_QUERY + System.currentTimeMillis())
            if (response.isSuccessful && response.code() == 200) {
                saveLogInCredential()
                Resource.success(response.body()?.mails)
            } else {
                Resource.error(response.message(), null)
            }
        } catch (e: Exception) {
            Resource.error(
                e.localizedMessage
                    ?: "Couldn't connect to the servers. Check your internet connection", null
            )
        }
    }

    suspend fun isLoggedIn(): Boolean {
        var result = false
        dataStore.apply {
            readCredential(Constants.KEY_CREDENTIAL)?.let { credential ->
                if (credential != Constants.NO_CREDENTIAL) {
                    basicAuthInterceptor.credential = credential
                    result = true
                }
            }
            readCredential(Constants.KEY_TOKEN)?.let { token ->
                if (token != Constants.NO_TOKEN) {
                    basicAuthInterceptor.token = token
                    result = true
                }
            }
        }
        return result
    }

    suspend fun logOut() {
//        mailDao.deleteAllMails()
        basicAuthInterceptor.credential = Constants.NO_CREDENTIAL
        basicAuthInterceptor.token = Constants.NO_TOKEN
        saveLogInCredential()
//        saveLastSync(Constants.INBOX_URL, Constants.NO_LAST_SYNC)
//        saveLastSync(Constants.SENT_URL, Constants.NO_LAST_SYNC)
//        saveLastSync(Constants.DRAFT_URL, Constants.NO_LAST_SYNC)
//        saveLastSync(Constants.JUNK_URL, Constants.NO_LAST_SYNC)
//        saveLastSync(Constants.TRASH_URL, Constants.NO_LAST_SYNC)
    }

    suspend fun readLastSync(request: String) =
        dataStore.readCredential(Constants.KEY_LAST_SYNC + request)?.toLong()
            ?: Constants.NO_LAST_SYNC

    suspend fun saveLastSync(request: String, lastSync: Long) = dataStore.saveCredential(
        Constants.KEY_LAST_SYNC + request, lastSync.toString()
    )

    suspend fun syncMails(lastSync: Long) =
        mailApi.getMails(Constants.INBOX_URL, Constants.UPDATE_QUERY + lastSync)

    private suspend fun saveLogInCredential() {
        dataStore.saveCredential(Constants.KEY_CREDENTIAL, basicAuthInterceptor.credential)
        dataStore.saveCredential(Constants.KEY_TOKEN, basicAuthInterceptor.token)
    }

    private suspend fun getAttachments(id: String): String = withContext(Dispatchers.IO) {
        val parsedMail = mailApi.getMailItemBody(Constants.MESSAGE_URL, id, "0").string()
        return@withContext Jsoup.parse(parsedMail).getElementById("iframeBody")
            ?.getElementsByTag("table")
            .toString()
    }

    private fun getBox(request: String) = when (request) {
        Constants.INBOX_URL -> 2
        Constants.TRASH_URL -> 3
        Constants.JUNK_URL -> 4
        Constants.SENT_URL -> 5
        Constants.DRAFT_URL -> 6
        else -> 0
    }.toString()

    fun getToken() = basicAuthInterceptor.token
}