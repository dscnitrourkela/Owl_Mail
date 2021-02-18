package github.sachin2dehury.owlmail.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import github.sachin2dehury.owlmail.api.data.Mail

@ExperimentalPagingApi
class MailRemoteMediator : RemoteMediator<Int,Mail>() {
    override suspend fun load(loadType: LoadType, state: PagingState<Int, Mail>): MediatorResult {
        TODO("Not yet implemented")
    }
}