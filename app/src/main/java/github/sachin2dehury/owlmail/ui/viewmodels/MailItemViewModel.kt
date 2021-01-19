package github.sachin2dehury.owlmail.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import github.sachin2dehury.owlmail.api.data.Mail
import github.sachin2dehury.owlmail.others.Event
import github.sachin2dehury.owlmail.others.Resource
import github.sachin2dehury.owlmail.repository.Repository

class MailItemViewModel @ViewModelInject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _id = MutableLiveData("0")
    val id: LiveData<String> = _id

    val token = repository.getToken().substringAfter('=')

    private val _forceUpdate = MutableLiveData(false)

    private val _parsedMail = _forceUpdate.switchMap {
        repository.getParsedMailItem(id.value!!)
            .asLiveData(viewModelScope.coroutineContext)
            .switchMap {
                MutableLiveData(Event(it))
            }
    }

    val parsedMail: LiveData<Event<Resource<Mail>>> = _parsedMail

    fun syncParsedMails() = _forceUpdate.postValue(true)

    fun setId(id: String) {
        _id.postValue(id)
    }
}