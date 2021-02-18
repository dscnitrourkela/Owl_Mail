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

    private val _conversationId = MutableLiveData<Int>()

    val parsedMails = _conversationId.switchMap { conversationId ->
        mailRepository.getParsedMails(conversationId).asLiveData(viewModelScope.coroutineContext)
            .switchMap { MutableLiveData(Event(it)) }
    }

//    private val _quota = MutableLiveData<String>()

    fun setConversationId(conversationId: Int) = _conversationId.postValue(conversationId)

//    fun setQuota(quota: String) = _quota.postValue(quota)

    fun getToken() = mailRepository.getToken()
}