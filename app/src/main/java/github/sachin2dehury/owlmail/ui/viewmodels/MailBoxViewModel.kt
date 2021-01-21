package github.sachin2dehury.owlmail.ui.viewmodels

import android.text.format.DateUtils
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import github.sachin2dehury.owlmail.api.data.Mail
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.others.Event
import github.sachin2dehury.owlmail.others.Resource
import github.sachin2dehury.owlmail.repository.Repository
import kotlinx.coroutines.launch

class MailBoxViewModel @ViewModelInject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _request = MutableLiveData(Constants.INBOX_URL)

    private val _lastSync = _request.switchMap { MutableLiveData(repository.readLastSync(it!!)) }

    private val _searchQuery = MutableLiveData(Constants.NO_CREDENTIAL)

    private val _search = _searchQuery.switchMap { searchQuery ->
        repository.getMails(_request.value!!, searchQuery!!)
            .asLiveData(viewModelScope.coroutineContext)
    }.switchMap {
        MutableLiveData(Event(it))
    }

    val search: LiveData<Event<Resource<List<Mail>>>> = _search

    private val _mails = _lastSync.switchMap { lastSync ->
        repository.getMails(_request.value!!, Constants.UPDATE_QUERY + lastSync!!)
            .asLiveData(viewModelScope.coroutineContext)
    }.switchMap {
        MutableLiveData(Event(it))
    }

    val mails: LiveData<Event<Resource<List<Mail>>>> = _mails

    fun saveLastSync() = viewModelScope.launch {
        repository.saveLastSync(
            _request.value!!,
            System.currentTimeMillis() - DateUtils.MINUTE_IN_MILLIS
        )
    }

    fun syncAllMails() = _request.postValue(_request.value)

    fun setRequest(request: String) = _request.postValue(request)

    fun setSearchQuery(query: String) = _searchQuery.postValue(query)
}