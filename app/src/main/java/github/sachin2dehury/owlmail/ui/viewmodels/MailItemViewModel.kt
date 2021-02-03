package github.sachin2dehury.owlmail.ui.viewmodels

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import github.sachin2dehury.owlmail.others.Event
import github.sachin2dehury.owlmail.repository.MailRepository
import javax.inject.Inject

@HiltViewModel
class MailItemViewModel @Inject constructor(
    private val mailRepository: MailRepository
) : ViewModel() {

    private val _id = MutableLiveData<String>()

    val parsedMail = _id.switchMap { id ->
        mailRepository.getParsedMailItem(id).asLiveData(viewModelScope.coroutineContext)
            .switchMap { MutableLiveData(Event(it)) }
    }

    fun setId(id: String) = _id.postValue(id)
}