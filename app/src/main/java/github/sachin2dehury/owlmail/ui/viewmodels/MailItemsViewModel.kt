package github.sachin2dehury.owlmail.ui.viewmodels

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import github.sachin2dehury.owlmail.others.Event
import github.sachin2dehury.owlmail.repository.MailRepository
import javax.inject.Inject

@HiltViewModel
class MailItemsViewModel @Inject constructor(
    private val mailRepository: MailRepository
) : ViewModel() {

    private val _conversationId = MutableLiveData<String>()

    val parsedMails = _conversationId.switchMap { conversationId ->
        mailRepository.getParsedMails(conversationId).asLiveData(viewModelScope.coroutineContext)
            .switchMap { MutableLiveData(Event(it)) }
    }

    fun setConversationId(conversationId: String) = _conversationId.postValue(conversationId)
}