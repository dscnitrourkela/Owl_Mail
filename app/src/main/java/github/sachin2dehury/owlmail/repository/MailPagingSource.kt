package github.sachin2dehury.owlmail.repository

import android.annotation.SuppressLint
import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.api.calls.MailApi
import github.sachin2dehury.owlmail.api.data.Mail
import github.sachin2dehury.owlmail.api.database.MailDao
import github.sachin2dehury.owlmail.others.ApiConstants
import github.sachin2dehury.owlmail.utils.isInternetConnected
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

class MailPagingSource(
    private val box: Byte,
    private val context: Context,
    private val request: String,
    private val mailApi: MailApi,
    private val mailDao: MailDao
) : PagingSource<Int, Mail>() {
    override fun getRefreshKey(state: PagingState<Int, Mail>) = state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Mail> {
        val page = params.key ?: 0
        val result = getMails(box, request, page) ?: emptyList()
        return try {
            LoadResult.Page(result, null, if (page < 100) page + 1 else null)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private suspend fun getMails(box: Byte, request: String, page: Int) =
        when (isInternetConnected(context)) {
            true -> {
                val month = getMonth(page)
                val mails =
                    mailApi.getMails(request, ApiConstants.MONTH_QUERY + month).body()?.mails
                mails?.let { mailDao.insertMails(it) }
                mails
            }
            else -> mailDao.getMails(box).first().subList(page, page + 10)
        }

    @SuppressLint("SimpleDateFormat")
    private fun getMonth(page: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -page)
        val firstDay = calendar.getActualMinimum(Calendar.DAY_OF_MONTH)
        calendar.set(Calendar.DAY_OF_MONTH, firstDay)
        val simpleDateFormat = SimpleDateFormat(context.getString(R.string.zimbra_month_format))
        return simpleDateFormat.format(calendar.time)
    }
}