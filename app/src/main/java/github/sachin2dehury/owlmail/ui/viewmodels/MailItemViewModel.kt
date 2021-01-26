package github.sachin2dehury.owlmail.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import github.sachin2dehury.owlmail.others.Event
import github.sachin2dehury.owlmail.repository.MailRepository

class MailItemViewModel @ViewModelInject constructor(
    private val mailRepository: MailRepository
) : ViewModel() {

    private val _id = MutableLiveData<String>()

    val parsedMail = _id.switchMap { id ->
        mailRepository.getParsedMailItem(id!!)
            .asLiveData(viewModelScope.coroutineContext)
            .switchMap {
                MutableLiveData(Event(it))
            }
    }

    fun setId(id: String) = _id.postValue(id)
}