package github.sachin2dehury.nitrmail.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import github.sachin2dehury.nitrmail.api.data.entities.Mails
import github.sachin2dehury.nitrmail.parser.data.ParsedMail
import github.sachin2dehury.nitrmail.repository.MainRepository

class MainViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository,
) : ViewModel() {

//    val mails = appClient.mails
//
//    fun postCredential(userRoll: String, userPassword: String) {
//        val result = Credentials.basic(userRoll, userPassword)
//        appClient.setCredentials(result)
//    }

    private val _mails = MutableLiveData<Mails>()
    val mails: LiveData<Mails> = _mails

    private val _item = MutableLiveData<ParsedMail>()
    val item: LiveData<ParsedMail> = _item

    private val _credential = MutableLiveData<String>()
    val credential: LiveData<String> = _credential

}