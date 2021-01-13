package github.sachin2dehury.nitrmail.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import github.sachin2dehury.nitrmail.api.data.Mail
import github.sachin2dehury.nitrmail.others.Constants
import github.sachin2dehury.nitrmail.others.Event
import github.sachin2dehury.nitrmail.others.Resource
import github.sachin2dehury.nitrmail.others.debugLog
import github.sachin2dehury.nitrmail.repository.Repository
import kotlinx.coroutines.launch

class MailBoxViewModel @ViewModelInject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _request = MutableLiveData(Constants.INBOX_URL)
    val request: LiveData<String> = _request

    private val _currentTime = MutableLiveData(System.currentTimeMillis())

    private val _lastSync = MutableLiveData(_currentTime.value)

    private val _searchQuery = MutableLiveData(Constants.NO_CREDENTIAL)

    val searchQuery: LiveData<String> = _searchQuery

    private val _forceUpdate = MutableLiveData(false)

    private val _forceUpdateSearch = MutableLiveData(false)

    private val _search = MutableLiveData<Event<Resource<List<Mail>>>>()

    val search: LiveData<Event<Resource<List<Mail>>>> = _search

    private val _mails = _forceUpdate.switchMap {
        repository.getMails(request.value!!, Constants.UPDATE_QUERY + _lastSync.value!!)
            .asLiveData(viewModelScope.coroutineContext)
    }.switchMap {
        MutableLiveData(Event(it))
    }

    val mails: LiveData<Event<Resource<List<Mail>>>> = _mails

    var themeState = Constants.DARK_THEME

    fun saveLastSync() = viewModelScope.launch {
        repository.saveLastSync(request.value!!, _currentTime.value!!)
        debugLog("saveLastSync ViewModel : ${request.value} ${_currentTime.value}")
    }

    fun readLastSync() = viewModelScope.launch {
        _lastSync.postValue(repository.readLastSync(request.value!!))
        debugLog("readLastSync ViewModel : ${request.value} ${_lastSync.value}")
    }

    fun logout() = viewModelScope.launch { repository.logOut() }

    fun syncAllMails() = _forceUpdate.postValue(true)

    fun syncSearchMails() {
//        _forceUpdateSearch.postValue(true)
        _search.postValue(
            repository.getMails(request.value!!, _searchQuery.value!!)
                .asLiveData(viewModelScope.coroutineContext).switchMap {
                    MutableLiveData(Event(it))
                }.value
        )
    }

    fun setRequest(request: String) = _request.postValue(request)

    fun setLastSync() = _currentTime.postValue(System.currentTimeMillis())

    fun setSearchQuery(query: String) = _searchQuery.postValue(query)

    fun isLastSyncChanged() = _lastSync.value != _currentTime.value

    fun saveThemeState() = viewModelScope.launch { repository.saveThemeState(themeState) }

    fun readThemeState() = viewModelScope.launch { themeState = repository.readThemeState() }
}