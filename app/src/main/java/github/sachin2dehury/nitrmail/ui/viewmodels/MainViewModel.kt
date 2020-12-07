package github.sachin2dehury.nitrmail.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import github.sachin2dehury.nitrmail.api.calls.AppClient
import okhttp3.Credentials

class MainViewModel @ViewModelInject constructor(
    appClient: AppClient
) : ViewModel() {

    val mails = appClient.mails

    private val _credential = MutableLiveData<String>()
    val credential: LiveData<String> = _credential


    fun postCredential(userRoll: String, userPassword: String) {
        val result = Credentials.basic(userRoll, userPassword)
        _credential.postValue(result)
    }
}