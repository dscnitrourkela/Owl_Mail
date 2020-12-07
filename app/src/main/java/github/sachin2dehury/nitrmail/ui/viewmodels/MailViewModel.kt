package github.sachin2dehury.nitrmail.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import github.sachin2dehury.nitrmail.api.calls.AppClient

class MailViewModel @ViewModelInject constructor(
    appClient: AppClient
) : ViewModel() {

}