package github.sachin2dehury.nitrmail.repository

import android.app.Application
import github.sachin2dehury.nitrmail.api.calls.BasicAuthInterceptor
import github.sachin2dehury.nitrmail.api.calls.MailApi
import github.sachin2dehury.nitrmail.api.data.Mail
import github.sachin2dehury.nitrmail.api.database.MailDao
import github.sachin2dehury.nitrmail.others.*
import github.sachin2dehury.nitrmail.parser.data.ParsedMail
import github.sachin2dehury.nitrmail.parser.parsedmails.ParsedMailDao
import github.sachin2dehury.nitrmail.parser.util.MailParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class Repository @Inject constructor(
    private val basicAuthInterceptor: BasicAuthInterceptor,
    private val context: Application,
    private val dataStore: DataStoreExt,
    private val internetChecker: InternetChecker,
    private val mailApi: MailApi,
    private val mailDao: MailDao,
    private val networkBoundResource: NetworkBoundResource,
    private val parsedMailDao: ParsedMailDao
) {

    fun getParsedMailItem(
        id: String
    ): Flow<Resource<ParsedMail>> {
        return networkBoundResource.makeNetworkRequest(
            query = {
                parsedMailDao.getMailItem(id)
            },
            fetch = {
                mailApi.getMailItem(id)
            },
            saveFetchResult = { result ->
                result.string().byteInputStream().let {
                    val parsedMail = MailParser().parse(it)
                    parsedMail.id = id
                    parsedMailDao.insertMail(parsedMail)
                }
            },
            shouldFetch = {
                internetChecker.isInternetConnected(context)
            },
        )
    }

    fun getMails(request: String, lastSync: Long): Flow<Resource<List<Mail>>> {
        return networkBoundResource.makeNetworkRequest(
            query = {
                mailDao.getMails(request)
            },
            fetch = {
                mailApi.getMails(request, Constants.UPDATE_QUERY + lastSync)
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

    private suspend fun deleteAllMails() = mailDao.deleteMails()

    private suspend fun deleteAllParsedMails() = mailDao.deleteMails()

    suspend fun login(credential: String) = withContext(Dispatchers.IO) {
        basicAuthInterceptor.credential = credential
        try {
            val response =
                mailApi.login(Constants.UPDATE_QUERY + System.currentTimeMillis())
            if (response.isSuccessful && response.code() == 200) {
                Resource.success(response.body()?.mails)
            } else {
                Resource.error(response.message() ?: response.message(), null)
            }
        } catch (e: Exception) {
            Resource.error(
                e.localizedMessage
                    ?: "Couldn't connect to the servers. Check your internet connection", null
            )
        }
    }

    suspend fun logOut() {
        deleteAllMails()
        deleteAllParsedMails()
        basicAuthInterceptor.credential = Constants.NO_CREDENTIAL
        saveLogInCredential(Constants.NO_CREDENTIAL)
        saveLastSync(Constants.INBOX_URL, Constants.NO_LAST_SYNC)
        saveLastSync(Constants.SENT_URL, Constants.NO_LAST_SYNC)
        saveLastSync(Constants.DRAFT_URL, Constants.NO_LAST_SYNC)
        saveLastSync(Constants.JUNK_URL, Constants.NO_LAST_SYNC)
        saveLastSync(Constants.TRASH_URL, Constants.NO_LAST_SYNC)
    }

    suspend fun isLoggedIn(): Boolean {
        dataStore.readCredential(Constants.KEY_CREDENTIAL)?.let { credential ->
            if (credential != Constants.NO_CREDENTIAL) {
                basicAuthInterceptor.credential = credential
                return true
            }
        }
        return false
    }

    suspend fun saveLogInCredential(credential: String) =
        dataStore.saveCredential(Constants.KEY_CREDENTIAL, credential)

    suspend fun readLastSync(request: String) =
        dataStore.readCredential(Constants.KEY_LAST_SYNC + request)?.toLong()
            ?: Constants.NO_LAST_SYNC

    suspend fun saveLastSync(request: String, lastSync: Long) = dataStore.saveCredential(
        Constants.KEY_LAST_SYNC + request, lastSync.toString()
    )
}