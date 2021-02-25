package github.sachin2dehury.owlmail.repository

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.api.calls.BasicAuthInterceptor
import github.sachin2dehury.owlmail.api.calls.MailApi
import github.sachin2dehury.owlmail.api.database.MailDao
import github.sachin2dehury.owlmail.api.database.ParsedMailDao
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.others.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MailRepository(
    private val basicAuthInterceptor: BasicAuthInterceptor,
    private val context: Context,
    private val mailApi: MailApi,
    private val mailDao: MailDao,
    private val parsedMailDao: ParsedMailDao,
    private val pagerConfig: PagingConfig,
) {

    @ExperimentalPagingApi
    fun getSearchMails(request: String) =
        Pager(pagerConfig, 0, MailRemoteMediator(request, mailApi, mailDao),
            { mailDao.searchMails(request) }).flow

    @ExperimentalPagingApi
    fun getParsedMails(conversationId: Int) =
        Pager(pagerConfig,
            0,
            ParsedMailRemoteMediator(conversationId, mailApi, mailDao, parsedMailDao),
            { parsedMailDao.getConversationMails(conversationId) }).flow

    @ExperimentalPagingApi
    fun getMails(request: String) =
        Pager(pagerConfig, 0, MailRemoteMediator(request, mailApi, mailDao),
            { mailDao.getMails(getBox(request)) }).flow

    private fun getBox(request: String) = when (request) {
        context.getString(R.string.inbox) -> 2
        context.getString(R.string.trash) -> 3
        context.getString(R.string.junk) -> 4
        context.getString(R.string.sent) -> 5
        context.getString(R.string.draft) -> 6
        else -> 0
    }

    suspend fun login() = try {
        val response = mailApi.login(
            context.getString(R.string.draft), Constants.AFTER_QUERY + System.currentTimeMillis()
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