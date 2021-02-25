package github.sachin2dehury.owlmail.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import github.sachin2dehury.owlmail.api.calls.MailApi
import github.sachin2dehury.owlmail.api.data.Mail
import github.sachin2dehury.owlmail.api.database.MailDao

@ExperimentalPagingApi
class SearchMailRemoteMediator(
    private val request: String,
    private val mailApi: MailApi,
    private val mailDao: MailDao
) : RemoteMediator<Int, Mail>() {
    override suspend fun load(loadType: LoadType, state: PagingState<Int, Mail>): MediatorResult {
        val page = when (loadType) {
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> (state.anchorPosition ?: 0) + 1
            LoadType.REFRESH -> state.anchorPosition ?: 0
        }
        return try {
            getSearchResult()
            MediatorResult.Success(true)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getSearchResult() {
        val response = mailApi.searchMails(request)
        val mails = response.body()?.mails
        mails?.forEach { mailDao.insertMail(it) }
    }
}