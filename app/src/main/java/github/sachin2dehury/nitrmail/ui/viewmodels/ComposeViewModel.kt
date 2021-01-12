package github.sachin2dehury.nitrmail.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import github.sachin2dehury.nitrmail.api.calls.MailViewClient
import github.sachin2dehury.nitrmail.repository.Repository

class ComposeViewModel @ViewModelInject constructor(
    private val repository: Repository,
    private val mailViewClient: MailViewClient
) : ViewModel() {
    val token = repository.getToken().substringAfter('=')

    fun getMailViewClient(): MailViewClient {
        mailViewClient.token = repository.getToken()
        return mailViewClient
    }
}