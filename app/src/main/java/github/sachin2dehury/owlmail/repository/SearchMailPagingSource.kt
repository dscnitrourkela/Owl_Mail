package github.sachin2dehury.owlmail.repository

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import github.sachin2dehury.owlmail.api.calls.MailApi
import github.sachin2dehury.owlmail.api.data.Mail
import github.sachin2dehury.owlmail.api.database.MailDao
import github.sachin2dehury.owlmail.utils.isInternetConnected
import kotlinx.coroutines.flow.first

class SearchMailPagingSource(
    private val context: Context,
    private val request: String,
    private val mailApi: MailApi,
    private val mailDao: MailDao
) : PagingSource<Int, Mail>() {
    override fun getRefreshKey(state: PagingState<Int, Mail>) = state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Mail> {
        val page = params.key ?: 0
        return try {
            val result = getMails(request, page) ?: emptyList()
            LoadResult.Page(result, null, page + 1)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private suspend fun getMails(request: String, page: Int) = when (isInternetConnected(context)) {
        true -> {
            val mails = mailApi.searchMails(request).body()?.mails
            mails?.let { mailDao.insertMails(it) }
            mails
        }
        else -> mailDao.searchMails(request).first().subList(page, page + 10)
    }
}