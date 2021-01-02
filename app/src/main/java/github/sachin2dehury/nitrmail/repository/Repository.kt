package github.sachin2dehury.nitrmail.repository

import android.app.Application
import android.util.Log
import github.sachin2dehury.nitrmail.api.calls.BasicAuthInterceptor
import github.sachin2dehury.nitrmail.api.calls.MailApi
import github.sachin2dehury.nitrmail.api.data.Mail
import github.sachin2dehury.nitrmail.api.database.MailDao
import github.sachin2dehury.nitrmail.others.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    private val basicAuthInterceptor: BasicAuthInterceptor,
    private val context: Application,
    private val dataStore: DataStoreExt,
    private val internetChecker: InternetChecker,
    private val mailApi: MailApi,
    private val mailDao: MailDao,
    private val networkBoundResource: NetworkBoundResource,
) {

    private suspend fun getHtml(id: String): Document = withContext(Dispatchers.IO) {
        val url =
            Constants.BASE_URL + Constants.MESSAGE_URL + "?id=" + id + "&xim=1&auth=co"
        Log.w("Test", url)
        return@withContext Jsoup.connect(url).header("Cookie", basicAuthInterceptor.token).get()
    }

    fun getParsedMailItem(
        id: String
    ): Flow<Resource<Mail>> {
        return networkBoundResource.makeNetworkRequest(
            query = {
                mailDao.getMailItem(id)
            },
            fetch = {
                mailApi.getMailItem(id)
//                getHtml(id)
            },
            saveFetchResult = { result ->
                val body = result.string()
//                val body = result.getElementsByClass("MsgBody").toString()
                Log.w("Test", result.toString())
                mailDao.updateMail(body, id)
            },
            shouldFetch = {
                internetChecker.isInternetConnected(context)
            },
        )
    }

    fun getMails(request: String, search: String): Flow<Resource<List<Mail>>> {
        Log.w("Test", "Syncing")
        return networkBoundResource.makeNetworkRequest(
            query = {
                mailDao.getMails(request)
            },
            fetch = {
                mailApi.getMails(request, search)
            },
            saveFetchResult = { response ->
                response.body()?.mails?.let { mails ->
                    mails.forEach {
                        it.box = request
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
        mailDao.deleteAllMails()
        basicAuthInterceptor.credential = Constants.NO_CREDENTIAL
        basicAuthInterceptor.token = Constants.NO_TOKEN
        saveLogInCredential()
        saveLastSync(Constants.INBOX_URL, Constants.NO_LAST_SYNC)
        saveLastSync(Constants.SENT_URL, Constants.NO_LAST_SYNC)
        saveLastSync(Constants.DRAFT_URL, Constants.NO_LAST_SYNC)
        saveLastSync(Constants.JUNK_URL, Constants.NO_LAST_SYNC)
        saveLastSync(Constants.TRASH_URL, Constants.NO_LAST_SYNC)
    }

    suspend fun saveLogInCredential() {
        dataStore.saveCredential(Constants.KEY_CREDENTIAL, basicAuthInterceptor.credential)
        dataStore.saveCredential(Constants.KEY_TOKEN, basicAuthInterceptor.token)
    }

    suspend fun readLastSync(request: String) =
        dataStore.readCredential(Constants.KEY_LAST_SYNC + request)?.toLong()
            ?: Constants.NO_LAST_SYNC

    suspend fun saveLastSync(request: String, lastSync: Long) = dataStore.saveCredential(
        Constants.KEY_LAST_SYNC + request, lastSync.toString()
    )

    fun getToken() = basicAuthInterceptor.token
}