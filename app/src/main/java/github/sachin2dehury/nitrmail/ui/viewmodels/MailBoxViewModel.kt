package github.sachin2dehury.nitrmail.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import github.sachin2dehury.nitrmail.api.data.Mail
import github.sachin2dehury.nitrmail.others.Constants
import github.sachin2dehury.nitrmail.others.Event
import github.sachin2dehury.nitrmail.others.Resource
import github.sachin2dehury.nitrmail.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MailBoxViewModel @ViewModelInject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _request = MutableLiveData(Constants.INBOX_URL)
    val request: LiveData<String> = _request

    private val _lastSync = MutableLiveData(System.currentTimeMillis())

    private val _searchQuery = MutableLiveData(Constants.UPDATE_QUERY + System.currentTimeMillis())
    val searchQuery: LiveData<String> = _searchQuery

    private val _forceUpdate = MutableLiveData(false)

    private val _forceUpdateSearch = MutableLiveData(false)

    private val _mails = _forceUpdate.switchMap {
        repository.getMails(request.value!!, Constants.UPDATE_QUERY + _lastSync.value!!)
            .asLiveData(viewModelScope.coroutineContext)
    }.switchMap {
        MutableLiveData(Event(it))
    }

    val mails: LiveData<Event<Resource<List<Mail>>>> = _mails

    private val _search = _forceUpdateSearch.switchMap {
        repository.getMails(request.value!!, searchQuery.value!!)
            .asLiveData(viewModelScope.coroutineContext).switchMap {
                MutableLiveData(Event(it))
            }
    }

    val search: LiveData<Event<Resource<List<Mail>>>> = _search

    fun saveLastSync() = viewModelScope.launch {
        repository.saveLastSync(request.value!!, _lastSync.value!!)
    }

    fun readLastSync() = viewModelScope.launch {
        _lastSync.postValue(repository.readLastSync(request.value!!))
    }

    fun logOut() = CoroutineScope(Dispatchers.IO).launch { repository.logOut() }

    fun syncAllMails() = _forceUpdate.postValue(true)

    fun syncSearchMails() = _forceUpdateSearch.postValue(true)

    fun setRequest(string: String) = _request.postValue(string)

    fun setLastSync(lastSync: Long) = _lastSync.postValue(lastSync)

    fun setSearchQuery(query: String) = _searchQuery.postValue(query)
}