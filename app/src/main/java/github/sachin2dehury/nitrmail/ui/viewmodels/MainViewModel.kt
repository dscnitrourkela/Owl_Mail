package github.sachin2dehury.nitrmail.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import github.sachin2dehury.nitrmail.api.data.entities.Mail
import github.sachin2dehury.nitrmail.others.Event
import github.sachin2dehury.nitrmail.others.Resource
import github.sachin2dehury.nitrmail.repository.MainRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private val _request = MutableLiveData<String>()
    val request: LiveData<String> = _request

//    private val _mails = MutableLiveData<Resource<List<Mail>>>()
//    val mails: LiveData<Resource<List<Mail>>> = _mails

    private val _forceUpdate = MutableLiveData(false)

    private val _mails = _forceUpdate.switchMap {
        repository.getAllMails().asLiveData(viewModelScope.coroutineContext)
    }.switchMap {
        MutableLiveData(Event(it))
    }
    val mails: LiveData<Event<Resource<List<Mail>>>> = this._mails

    fun syncAllMails() = _forceUpdate.postValue(true)

    fun insertMails() = GlobalScope.launch {
        val result = mails.value?.peekContent()?.data
        result?.let {
            repository.insertMails(it)
        }
//        val result = mails.value?.data
//        result.let {
//            if (it != null) {
//                repository.insertMails(it)
//            }
//        }
    }
}