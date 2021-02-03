package github.sachin2dehury.owlmail.ui.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import github.sachin2dehury.owlmail.repository.MailRepository
import javax.inject.Inject

@HiltViewModel
class ComposeViewModel @Inject constructor(
    private val mailRepository: MailRepository
) : ViewModel() {
    val token = mailRepository.getToken()
}