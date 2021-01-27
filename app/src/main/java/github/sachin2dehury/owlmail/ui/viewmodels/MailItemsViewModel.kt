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
        mailRepository.getParsedMails(conversationId).asLiveData(viewModelScope.coroutineContext)
            .switchMap { MutableLiveData(Event(it)) }.also {
                it.value?.peekContent()?.data?.let { list ->
                    list.forEach { mail -> mailRepository.getParsedMailItem(mail.id) }
                }
            }
    }

    fun setConversationId(conversationId: String) = _conversationId.postValue(conversationId)
}