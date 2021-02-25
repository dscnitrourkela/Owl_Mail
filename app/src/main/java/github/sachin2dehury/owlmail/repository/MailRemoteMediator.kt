package github.sachin2dehury.owlmail.repository

import android.annotation.SuppressLint
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import github.sachin2dehury.owlmail.api.calls.MailApi
import github.sachin2dehury.owlmail.api.data.Mail
import github.sachin2dehury.owlmail.api.database.MailDao
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.others.debugLog
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalPagingApi
class MailRemoteMediator(
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
        debugLog(page)
        return try {
            getMails(page)
            MediatorResult.Success(true)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getMails(page: Int) {
        val month = getMonth(page)
        val response = mailApi.getMails(request, Constants.MONTH_QUERY + month)
        val mails = response.body()?.mails
        mails?.forEach { mailDao.insertMail(it) }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getMonth(page: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -page)
        val firstDay = calendar.getActualMinimum(Calendar.DAY_OF_MONTH)
        calendar.set(Calendar.DAY_OF_MONTH, firstDay)
        val simpleDateFormat = SimpleDateFormat("MM/dd/yyyy")
        return simpleDateFormat.format(calendar.time)
    }
}