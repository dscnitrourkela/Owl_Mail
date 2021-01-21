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

    private val _id = MutableLiveData<String>("")

    private val _parsedMail = _id.switchMap { id ->
        repository.getParsedMailItem(id!!)
            .asLiveData(viewModelScope.coroutineContext)
            .switchMap {
                MutableLiveData(Event(it))
            }
    }

    val parsedMail: LiveData<Event<Resource<Mail>>> = _parsedMail

    fun syncParsedMails() = _id.postValue(_id.value)

    fun setId(id: String) = _id.postValue(id)
}