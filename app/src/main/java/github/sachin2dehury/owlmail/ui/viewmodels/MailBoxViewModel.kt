package github.sachin2dehury.owlmail.ui.viewmodels

import android.text.format.DateUtils
import androidx.lifecycle.*
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.others.Event
import github.sachin2dehury.owlmail.others.debugLog
import github.sachin2dehury.owlmail.repository.Repository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

abstract class MailBoxViewModel(
//@ViewModelInject constructor(
    private val repository: Repository
) : ViewModel() {

    init {
        debugLog("MailBoxViewModel Created")
    }

    override fun onCleared() {
        debugLog("MailBoxViewModel Dead")
        super.onCleared()
    }

    abstract val request: String

    private val _forceUpdate = MutableLiveData(false)

    private val _searchQuery = MutableLiveData(Constants.NO_CREDENTIAL)

    private val lastSync = _forceUpdate.switchMap {
        repository.readLastSync(request).map { it ?: Constants.NO_LAST_SYNC }
            .asLiveData(viewModelScope.coroutineContext)
    }

    val search = _searchQuery.switchMap {
        repository.getMails(request, _searchQuery.value!!)
            .asLiveData(viewModelScope.coroutineContext)
    }.switchMap {
        MutableLiveData(Event(it))
    }

    val mails = lastSync.switchMap {
        repository.getMails(request, Constants.UPDATE_QUERY + lastSync.value)
            .asLiveData(viewModelScope.coroutineContext)
    }.switchMap {
        MutableLiveData(Event(it))
    }

    fun saveLastSync() = viewModelScope.launch {
        repository.saveLastSync(
            request, System.currentTimeMillis() - DateUtils.MINUTE_IN_MILLIS
        )
    }

    fun syncAllMails() = _forceUpdate.postValue(true)

//    fun setRequest(request: String) = this.request.postValue(request)

    fun setSearchQuery(query: String) = _searchQuery.postValue(query)
}