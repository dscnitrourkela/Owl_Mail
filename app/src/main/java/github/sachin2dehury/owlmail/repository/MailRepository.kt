package github.sachin2dehury.owlmail.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.api.calls.BasicAuthInterceptor
import github.sachin2dehury.owlmail.api.calls.MailApi
import github.sachin2dehury.owlmail.api.data.Mails
import github.sachin2dehury.owlmail.api.data.ParsedMail
import github.sachin2dehury.owlmail.api.database.MailDao
import github.sachin2dehury.owlmail.api.database.ParsedMailDao
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.others.Resource
import github.sachin2dehury.owlmail.utils.isInternetConnected
import github.sachin2dehury.owlmail.utils.networkBoundResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import retrofit2.Response

class MailRepository(
    private val basicAuthInterceptor: BasicAuthInterceptor,
    private val context: Context,
    private val mailApi: MailApi,
    private val mailDao: MailDao,
    private val parsedMailDao: ParsedMailDao,
    private val pagerConfig: PagingConfig,
) {

    fun getMails(request: String, lastSync: Long) = networkBoundResource(
        query = { getPager(mailDao.getMails(getBox(request))) },
        fetch = { mailApi.getMails(request, Constants.UPDATE_QUERY + lastSync) },
        saveFetchResult = { response -> insertMails(response) },
        shouldFetch = { isInternetConnected(context) },
    )

    fun getParsedMails(conversationId: Int) = networkBoundResource(
        query = { getPager(parsedMailDao.getConversationMails(conversationId)) },
        fetch = { mailDao.getMailsId(conversationId) },
        saveFetchResult = { response -> insertParsedMails(response, conversationId) },
        shouldFetch = { isInternetConnected(context) },
    )

    fun searchMails(search: String) = networkBoundResource(
        query = { getPager(mailDao.searchMails(search)) },
        fetch = { mailApi.searchMails(search) },
        saveFetchResult = { },
        shouldFetch = { false },
    )

    private fun getBox(request: String) = when (request) {
        context.getString(R.string.inbox) -> 2
        context.getString(R.string.trash) -> 3
        context.getString(R.string.junk) -> 4
        context.getString(R.string.sent) -> 5
        context.getString(R.string.draft) -> 6
        else -> 0
    }

    private fun <T : Any> getPager(pagingSourceFactory: PagingSource<Int, T>) =
        flowOf(Pager(pagerConfig, pagingSourceFactory = { pagingSourceFactory }))

    private suspend fun insertMails(response: Response<Mails>) =
        response.body()?.mails?.let { mails -> mails.forEach { mail -> mailDao.insertMail(mail) } }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun insertParsedMails(ids: Flow<List<Int>>, conversationId: Int) =
        ids.first().forEach { id ->
            val response = mailApi.getParsedMail(id).string()
            val parsedMail = ParsedMail(id, conversationId, Jsoup.parse(response))
            parsedMailDao.insertMail(parsedMail)
        }

    suspend fun login() = try {
        val response = mailApi.login(
            context.getString(R.string.draft), Constants.UPDATE_QUERY + System.currentTimeMillis()
        )
        if (response.isSuccessful && response.code() == 200) {
            Resource.success(response.body()?.mails)
        } else {
            Resource.error(response.message(), null)
        }
    } catch (e: Exception) {
        Resource.error(
            e.message ?: "Couldn't connect to the servers. Check your internet connection", null
        )
    }

    fun setCredential(credential: String) {
        basicAuthInterceptor.credential = credential
    }

    fun getCredential() = basicAuthInterceptor.credential

    fun setToken(token: String) {
        basicAuthInterceptor.token = token
    }

    fun getToken() = basicAuthInterceptor.token

    fun resetLogin() = CoroutineScope(Dispatchers.IO).launch {
        mailDao.deleteAllMails()
        parsedMailDao.deleteAllMails()
        basicAuthInterceptor.credential = Constants.NO_CREDENTIAL
        basicAuthInterceptor.token = Constants.NO_TOKEN
    }
}