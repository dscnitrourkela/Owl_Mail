package github.sachin2dehury.owlmail.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import github.sachin2dehury.owlmail.others.Event
import github.sachin2dehury.owlmail.repository.MailRepository

class MailItemsViewModel @ViewModelInject constructor(
    private val mailRepository: MailRepository
) : ViewModel() {

    private val _conversationId = MutableLiveData<String>()

    val parsedMails = _conversationId.switchMap { conversationId ->
        mailRepository.getParsedMails(conversationId)
            .asLiveData(viewModelScope.coroutineContext)
            .switchMap {
                MutableLiveData(Event(it))
            }
    }

    fun syncParsedMails() = _conversationId.postValue(_conversationId.value)

    fun setConversationId(conversationId: String) = _conversationId.postValue(conversationId)
}