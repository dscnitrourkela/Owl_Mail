package github.sachin2dehury.nitrmail.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import github.sachin2dehury.nitrmail.api.calls.AppClient

class MailBoxViewModel @ViewModelInject constructor(
    private val appClient: AppClient
) : ViewModel() {

    val mails = appClient.mails

    init {

    }
}