package github.sachin2dehury.owlmail.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import github.sachin2dehury.owlmail.api.calls.MailApi
import github.sachin2dehury.owlmail.api.data.ParsedMail
import github.sachin2dehury.owlmail.api.database.MailDao
import github.sachin2dehury.owlmail.api.database.ParsedMailDao
import kotlinx.coroutines.flow.first
import org.jsoup.Jsoup

@ExperimentalPagingApi
class ParsedMailRemoteMediator(
    private val conversationId: Int,
    private val mailApi: MailApi,
    private val mailDao: MailDao,
    private val parsedMailDao: ParsedMailDao
) : RemoteMediator<Int, ParsedMail>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ParsedMail>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> (state.anchorPosition ?: 0) + 1
            LoadType.REFRESH -> state.anchorPosition ?: 0
        }
        return try {
            getParsedMails()
            MediatorResult.Success(true)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getParsedMails() {
        val idList = mailDao.getMailsId(conversationId).first()
        idList.forEach {
            val response = mailApi.getParsedMail(it)
            val document = Jsoup.parse(response.string())
            val parsedMail = ParsedMail(it, conversationId, document)
            parsedMailDao.insertMail(parsedMail)
        }
    }
}