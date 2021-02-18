package github.sachin2dehury.owlmail.repository

import android.content.Context
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.api.calls.BasicAuthInterceptor
import github.sachin2dehury.owlmail.api.calls.MailApi
import github.sachin2dehury.owlmail.api.data.Mails
import github.sachin2dehury.owlmail.api.database.MailDao
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.others.Resource
import github.sachin2dehury.owlmail.utils.isInternetConnected
import github.sachin2dehury.owlmail.utils.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class MailRepository(
    private val basicAuthInterceptor: BasicAuthInterceptor,
    private val context: Context,
    private val mailApi: MailApi,
    private val mailDao: MailDao,
) {

    fun getMails(request: String, lastSync: Long) = networkBoundResource(
        query = { mailDao.getMails(getBox(request)) },
        fetch = { mailApi.getMails(request, Constants.UPDATE_QUERY + lastSync) },
        saveFetchResult = { response -> insertMails(response) },
        shouldFetch = { isInternetConnected(context) },
    )

    fun getParsedMails(conversationId: Int) = networkBoundResource(
        query = { mailDao.getConversationMails(conversationId) },
        fetch = { mailDao.getMailsId(conversationId) },
        saveFetchResult = { response -> updateConversations(response) },
        shouldFetch = { isInternetConnected(context) },
    )

    fun searchMails(search: String) = networkBoundResource(
        query = { mailDao.searchMails(search) },
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

    private suspend fun insertMails(response: Response<Mails>) =
        response.body()?.mails?.let { mails -> mails.forEach { mail -> mailDao.insertMail(mail) } }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun updateConversations(ids: Flow<List<Int>>) = ids.first().forEach { id ->
        val parsedMail = mailApi.getParsedMail(id).string()
        mailDao.updateMail(parsedMail, id)
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

    fun resetLogin() = flow {
        emit(mailDao.deleteAllMails())
        basicAuthInterceptor.credential = Constants.NO_CREDENTIAL
        basicAuthInterceptor.token = Constants.NO_TOKEN
    }

}