package github.sachin2dehury.nitrmail.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import github.sachin2dehury.nitrmail.api.calls.AppClient
import okhttp3.Credentials

class MainViewModel @ViewModelInject constructor(
    private val appClient: AppClient
) : ViewModel() {

    val mails = appClient.mails

    fun postCredential(userRoll: String, userPassword: String) {
        val result = Credentials.basic(userRoll, userPassword)
        appClient.setCredentials(result)
    }
}