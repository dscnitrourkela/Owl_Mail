package github.sachin2dehury.owlmail.ui.viewmodels

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import github.sachin2dehury.owlmail.others.Event
import github.sachin2dehury.owlmail.repository.MailRepository
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val mailRepository: MailRepository,
) : ViewModel() {

    private val _searchQuery = MutableLiveData<String>()

    val search = _searchQuery.switchMap { query ->
        mailRepository.searchMails(query).asLiveData(viewModelScope.coroutineContext)
    }.switchMap {
        MutableLiveData(Event(it))
    }

    fun syncSearchMails(query: String) = _searchQuery.postValue(query)
}