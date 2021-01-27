package github.sachin2dehury.owlmail.ui.viewmodels

import android.text.format.DateUtils
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.others.Event
import github.sachin2dehury.owlmail.repository.DataStoreRepository
import github.sachin2dehury.owlmail.repository.MailRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MailBoxViewModel @ViewModelInject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val mailRepository: MailRepository,
) : ViewModel() {

    private val _request = MutableLiveData<String>()

    private val _searchQuery = MutableLiveData<String>()

    private val lastSync = _request.switchMap { request ->
        dataStoreRepository.readLastSync(request).map { it ?: Constants.NO_LAST_SYNC }
            .asLiveData(viewModelScope.coroutineContext)
    }

    val search = _searchQuery.switchMap { request ->
        mailRepository.getMails(request, _searchQuery.value ?: Constants.NO_CREDENTIAL)
            .asLiveData(viewModelScope.coroutineContext)
    }.switchMap {
        MutableLiveData(Event(it))
    }

    val mails = lastSync.switchMap { lastSync ->
        mailRepository.getMails(_request.value ?: "", Constants.UPDATE_QUERY + lastSync)
            .asLiveData(viewModelScope.coroutineContext)
    }.switchMap {
        MutableLiveData(Event(it))
    }

    fun saveLastSync() = viewModelScope.launch {
        dataStoreRepository.saveLastSync(
            _request.value ?: "", System.currentTimeMillis() - DateUtils.MINUTE_IN_MILLIS
        )
    }

    fun syncAllMails(request: String) = _request.postValue(request)

    fun syncSearchMails(query: String) = _searchQuery.postValue(query)
}