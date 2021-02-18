package github.sachin2dehury.owlmail.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import github.sachin2dehury.owlmail.api.data.Mail

class MailPagingSource: PagingSource<Int,Mail>() {
    override fun getRefreshKey(state: PagingState<Int, Mail>): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Mail> {
        TODO("Not yet implemented")
    }
}