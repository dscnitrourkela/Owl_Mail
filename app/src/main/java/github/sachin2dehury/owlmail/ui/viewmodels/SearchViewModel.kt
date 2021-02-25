package github.sachin2dehury.owlmail.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import github.sachin2dehury.owlmail.repository.MailRepository
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val mailRepository: MailRepository
) : ViewModel() {

    @ExperimentalPagingApi
    fun getSearchMails(request: String) =
        mailRepository.getSearchMails(request).cachedIn(viewModelScope)
}