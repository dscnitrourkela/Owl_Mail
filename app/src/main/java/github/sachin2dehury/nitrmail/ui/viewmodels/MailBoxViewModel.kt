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

    var lastSync = System.currentTimeMillis()

    private val _forceUpdate = MutableLiveData(false)

    private val _mails = _forceUpdate.switchMap {
        repository.getMails(request.value!!, Constants.UPDATE_QUERY + lastSync)
            .asLiveData(viewModelScope.coroutineContext)
    }.switchMap {
        MutableLiveData(Event(it))
    }
    val mails: LiveData<Event<Resource<List<Mail>>>> = _mails

    private val _search = MutableLiveData<Event<Resource<List<Mail>>>>()

    val search: LiveData<Event<Resource<List<Mail>>>> = _search

    fun saveLastSync() = viewModelScope.launch {
        repository.saveLastSync(request.value!!, lastSync)
    }

    fun readLastSync() = viewModelScope.launch {
        lastSync = repository.readLastSync(request.value!!)
    }

    fun logOut() = CoroutineScope(Dispatchers.IO).launch { repository.logOut() }

    fun syncAllMails() = _forceUpdate.postValue(true)

    fun setRequest(string: String) = _request.postValue(string)

    fun searchMails(search: String) {
        _search.postValue(
            repository.getMails(request.value!!, search)
                .asLiveData(viewModelScope.coroutineContext).switchMap {
                    MutableLiveData(Event(it))
                }.value
        )
    }
}