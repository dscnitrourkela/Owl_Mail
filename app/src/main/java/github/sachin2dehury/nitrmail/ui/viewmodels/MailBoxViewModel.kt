package github.sachin2dehury.nitrmail.ui.viewmodels

import androidx.lifecycle.*
import github.sachin2dehury.nitrmail.api.data.Mail
import github.sachin2dehury.nitrmail.others.Constants
import github.sachin2dehury.nitrmail.others.Event
import github.sachin2dehury.nitrmail.others.Resource
import github.sachin2dehury.nitrmail.repository.Repository
import kotlinx.coroutines.launch

abstract class MailBoxViewModel(
    private val repository: Repository
) : ViewModel() {

    abstract val request: String

    private val _currentTime = MutableLiveData(System.currentTimeMillis())

    private val _lastSync = MutableLiveData(_currentTime.value)

    private val _searchQuery = MutableLiveData(Constants.NO_CREDENTIAL)

    val searchQuery: LiveData<String> = _searchQuery

    private val _forceUpdate = MutableLiveData(false)

    private val _forceUpdateSearch = MutableLiveData(false)

    private val _search = _forceUpdateSearch.switchMap {
        repository.getMails(request, Constants.UPDATE_QUERY + _lastSync.value!!)
            .asLiveData(viewModelScope.coroutineContext)
    }.switchMap {
        MutableLiveData(Event(it))
    }

    val search: LiveData<Event<Resource<List<Mail>>>> = _search

    private val _mails = _forceUpdate.switchMap {
        repository.getMails(request, Constants.UPDATE_QUERY + _lastSync.value!!)
            .asLiveData(viewModelScope.coroutineContext)
    }.switchMap {
        MutableLiveData(Event(it))
    }

    val mails: LiveData<Event<Resource<List<Mail>>>> = _mails

    fun saveLastSync() = viewModelScope.launch {
        repository.saveLastSync(request, _currentTime.value!!)
    }

    fun readLastSync() = viewModelScope.launch {
        _lastSync.postValue(repository.readLastSync(request))
    }

    fun syncAllMails() = _forceUpdate.postValue(true)

    fun syncSearchMails() = _forceUpdateSearch.postValue(true)

    fun setSearchQuery(query: String) = _searchQuery.postValue(query)

    fun setLastSync() = _currentTime.postValue(System.currentTimeMillis())
}