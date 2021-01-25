package github.sachin2dehury.owlmail.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import github.sachin2dehury.owlmail.repository.MailRepository

class ComposeViewModel @ViewModelInject constructor(
    private val mailRepository: MailRepository
) : ViewModel() {
    val token = mailRepository.getToken()
}