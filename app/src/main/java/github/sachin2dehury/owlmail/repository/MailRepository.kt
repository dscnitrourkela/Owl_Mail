package github.sachin2dehury.owlmail.repository

import android.content.Context
import github.sachin2dehury.owlmail.api.calls.BasicAuthInterceptor
import github.sachin2dehury.owlmail.api.calls.MailApi
import github.sachin2dehury.owlmail.api.data.Mails
import github.sachin2dehury.owlmail.api.database.MailDao
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.others.Resource
import github.sachin2dehury.owlmail.others.debugLog
import github.sachin2dehury.owlmail.utils.isInternetConnected
import github.sachin2dehury.owlmail.utils.networkBoundResource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.jsoup.Jsoup
import retrofit2.Response

class MailRepository(
    private val basicAuthInterceptor: BasicAuthInterceptor,
    private val context: Context,
    private val mailApi: MailApi,
    private val mailDao: MailDao,
) {

    fun getMails(request: String, search: String) = networkBoundResource(
        query = { mailDao.getMails(getBox(request)) },
        fetch = { mailApi.getMails(request, search) },
        saveFetchResult = { response -> insertMails(response) },
        shouldFetch = { isInternetConnected(context) },
    )

    fun getParsedMails(conversationId: String) = networkBoundResource(
        query = { mailDao.getConversationMails(conversationId) },
        fetch = { updateConversations(conversationId) },
        saveFetchResult = { },
        shouldFetch = { isInternetConnected(context) },
    )

    private fun getBox(request: String) = when (request) {
        Constants.INBOX_URL -> 2
        Constants.TRASH_URL -> 3
        Constants.JUNK_URL -> 4
        Constants.SENT_URL -> 5
        Constants.DRAFT_URL -> 6
        else -> 0
    }.toString()

    private suspend fun insertMails(response: Response<Mails>) =
        response.body()?.mails?.let { mails -> mails.forEach { mail -> mailDao.insertMail(mail) } }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun updateConversations(conversationId: String) {
        mailDao.getMailsId(conversationId).first().forEach { id ->
//            val body = mailApi.getMailBody(id).string()
            val token = getToken().substringAfter('=')
            val parsedMail = Jsoup.parse(mailApi.getParsedMail(id).string())
//            parsedMail.removeClass("MsgBody")
//            parsedMail.removeClass("Msg")
//            parsedMail.removeClass("ZhAppContent")
//            parsedMail.getElementsByClass("MsgHdr").remove()
            var body =
                "${parsedMail.select(".msgwrap")}<br>${parsedMail.select(".View.attachments")}"
            if (body.contains("auth=co", true)) {
                body = body.replace("auth=co", "auth=qp&amp;zauthtoken=$token")
                debugLog("Mail Body $body")
            }
            mailDao.updateMail(body, id)
        }
    }

    suspend fun login() = try {
        val response = mailApi.login(Constants.UPDATE_QUERY + System.currentTimeMillis())
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