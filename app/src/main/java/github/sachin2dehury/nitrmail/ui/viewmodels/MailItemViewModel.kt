package github.sachin2dehury.nitrmail.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import github.sachin2dehury.nitrmail.api.data.Mail
import github.sachin2dehury.nitrmail.others.Event
import github.sachin2dehury.nitrmail.others.Resource
import github.sachin2dehury.nitrmail.repository.Repository

class MailItemViewModel @ViewModelInject constructor(
    private val repository: Repository
) : ViewModel() {

    companion object {
        var id = ""
    }

    private val _forceUpdate = MutableLiveData(false)

    private val _parsedMail = _forceUpdate.switchMap {
        repository.getParsedMailItem(id).asLiveData(viewModelScope.coroutineContext).switchMap {
            MutableLiveData(Event(it))
        }
    }
    val parsedMail: LiveData<Event<Resource<Mail>>> = _parsedMail

    fun syncParsedMails() = _forceUpdate.postValue(true)

    fun getToken() = repository.getToken()
}