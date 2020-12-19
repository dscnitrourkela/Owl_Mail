package github.sachin2dehury.nitrmail.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import github.sachin2dehury.nitrmail.api.data.Mail
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

    private val _mails = MutableLiveData<Event<Resource<List<Mail>>>>()
    val mails: LiveData<Event<Resource<List<Mail>>>> = this._mails

    fun insertMails() = GlobalScope.launch {
        val result = mails.value?.peekContent()?.data
        result?.let {
            repository.insertMails(it)
        }
    }

    fun getMails() {
        _mails.postValue(Event(Resource.loading(null)))
        viewModelScope.launch {
            val result =
                repository.getMails(request.value!!).asLiveData(viewModelScope.coroutineContext)
            result.value?.let {
                _mails.postValue(Event(it))
            }
        }
    }

    fun setRequest(string: String) {
        _request.postValue(string)
    }
}