package github.sachin2dehury.owlmail.repository

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import github.sachin2dehury.owlmail.api.calls.MailApi
import github.sachin2dehury.owlmail.api.data.Mail
import github.sachin2dehury.owlmail.api.database.MailDao
import github.sachin2dehury.owlmail.utils.isInternetConnected
import kotlinx.coroutines.flow.first
import java.util.*

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
            getMails(request, page)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private suspend fun getMails(request: String, page: Int): LoadResult<Int, Mail> =
        when (isInternetConnected(context)) {
            true -> {
                val mails = mailApi.searchMails(request).body()?.mails
                mails?.let { mailDao.insertMails(it) }
                LoadResult.Page(
                    mails ?: emptyList(),
                    if (page > 0) page - 1 else null,
                    if (page < 100) page + 1 else null
                )
            }
            else -> {
                val mails = mailDao.searchMails(request).first()
                LoadResult.Page(mails, null, null)
            }
        }

    private fun getCalender(page: Int) = Calendar.getInstance().apply {
        add(Calendar.MONTH, -page)
        val firstDay = getActualMinimum(Calendar.DAY_OF_MONTH)
        set(Calendar.DAY_OF_MONTH, firstDay)
    }
}