package github.sachin2dehury.owlmail.ui.viewmodels

import android.text.format.DateUtils
import androidx.lifecycle.*
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.others.Event
import github.sachin2dehury.owlmail.repository.DataStoreRepository
import github.sachin2dehury.owlmail.repository.MailRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

abstract class MailBoxViewModel(
    private val dataStoreRepository: DataStoreRepository,
    private val mailRepository: MailRepository,
) : ViewModel() {

    abstract val request: String

    private val _forceUpdate = MutableLiveData(false)

    private val _searchQuery = MutableLiveData(Constants.NO_CREDENTIAL)

    private val lastSync = _forceUpdate.switchMap {
        dataStoreRepository.readLastSync(request).map { it ?: Constants.NO_LAST_SYNC }
            .asLiveData(viewModelScope.coroutineContext)
    }

    val search = _searchQuery.switchMap {
        mailRepository.getMails(request, _searchQuery.value!!)
            .asLiveData(viewModelScope.coroutineContext)
    }.switchMap {
        MutableLiveData(Event(it))
    }

    val mails = lastSync.switchMap {
        mailRepository.getMails(request, Constants.UPDATE_QUERY + lastSync.value)
            .asLiveData(viewModelScope.coroutineContext)
    }.switchMap {
        MutableLiveData(Event(it))
    }

    fun saveLastSync() = viewModelScope.launch {
        dataStoreRepository.saveLastSync(
            request, System.currentTimeMillis() - DateUtils.MINUTE_IN_MILLIS
        )
    }

    fun syncAllMails() = _forceUpdate.postValue(true)

    fun setSearchQuery(query: String) = _searchQuery.postValue(query)
}