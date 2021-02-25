package github.sachin2dehury.owlmail.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import github.sachin2dehury.owlmail.repository.MailRepository
import javax.inject.Inject

@HiltViewModel
class MailItemsViewModel @Inject constructor(
    private val mailRepository: MailRepository
) : ViewModel() {

    @ExperimentalPagingApi
    fun getParsedMails(conversationId: Int) =
        mailRepository.getParsedMails(conversationId).cachedIn(viewModelScope)

//    private val _quota = MutableLiveData<String>()

//    fun setQuota(quota: String) = _quota.postValue(quota)

    fun getToken() = mailRepository.getToken()
}