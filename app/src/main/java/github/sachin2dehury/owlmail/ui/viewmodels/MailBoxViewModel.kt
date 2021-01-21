package github.sachin2dehury.owlmail.ui.viewmodels

import android.text.format.DateUtils
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.others.Event
import github.sachin2dehury.owlmail.repository.Repository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MailBoxViewModel @ViewModelInject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _forceUpdate = MutableLiveData(false)

    private val _request = MutableLiveData(Constants.INBOX_URL)

    private val _searchQuery = MutableLiveData(Constants.NO_CREDENTIAL)

    private val lastSync = _forceUpdate.switchMap {
        repository.readLastSync(_request.value!!).map { it ?: Constants.NO_LAST_SYNC }
            .asLiveData(viewModelScope.coroutineContext)
    }

    val search = _searchQuery.switchMap { searchQuery ->
        repository.getMails(_request.value!!, searchQuery!!)
            .asLiveData(viewModelScope.coroutineContext)
    }.switchMap {
        MutableLiveData(Event(it))
    }

    val mails = _request.switchMap {
        syncAllMails()
        repository.getMails(_request.value!!, Constants.UPDATE_QUERY + lastSync.value)
            .asLiveData(viewModelScope.coroutineContext)
    }.switchMap {
        MutableLiveData(Event(it))
    }

    fun saveLastSync() = viewModelScope.launch {
        repository.saveLastSync(
            _request.value!!,
            System.currentTimeMillis() - DateUtils.MINUTE_IN_MILLIS
        )
    }

    fun syncAllMails() = _forceUpdate.postValue(true)

    fun setRequest(request: String) = _request.postValue(request)

    fun setSearchQuery(query: String) = _searchQuery.postValue(query)
}