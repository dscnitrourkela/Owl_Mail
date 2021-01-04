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

    private val _id = MutableLiveData<String>()
    val id: LiveData<String> = _id

    private val _hasAttachments = MutableLiveData(false)

    private val _parsedMail = MutableLiveData<Event<Resource<Mail>>>()
    val parsedMail: LiveData<Event<Resource<Mail>>> = _parsedMail

    fun syncParsedMails() {
        _parsedMail.postValue(
            repository.getParsedMailItem(id.value!!, _hasAttachments.value!!)
                .asLiveData(viewModelScope.coroutineContext)
                .switchMap {
                    MutableLiveData(Event(it))
                }.value
        )
    }

    fun setId(id: String, hasAttachments: Boolean) {
        _id.postValue(id)
        _hasAttachments.postValue(hasAttachments)
    }
}