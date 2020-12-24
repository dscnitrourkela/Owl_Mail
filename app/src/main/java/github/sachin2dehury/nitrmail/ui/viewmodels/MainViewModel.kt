package github.sachin2dehury.nitrmail.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import github.sachin2dehury.nitrmail.api.data.mails.Mail
import github.sachin2dehury.nitrmail.others.Constants
import github.sachin2dehury.nitrmail.others.Event
import github.sachin2dehury.nitrmail.others.Resource
import github.sachin2dehury.nitrmail.repository.MainRepository

class MainViewModel @ViewModelInject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private val _request = MutableLiveData<String>()
    val request: LiveData<String> = _request

    var lastSync = Constants.NO_LAST_SYNC

    private val _forceUpdate = MutableLiveData(false)

    private val _mails = _forceUpdate.switchMap {
        repository.getMails(request.value!!, lastSync).asLiveData(viewModelScope.coroutineContext)
    }.switchMap {
        MutableLiveData(Event(it))
    }
    val mails: LiveData<Event<Resource<List<Mail>>>> = _mails

    fun syncAllNotes() {
        lastSync = System.currentTimeMillis()
        _forceUpdate.postValue(true)
    }

    fun setRequest(string: String) {
        _request.postValue(string)
    }
}