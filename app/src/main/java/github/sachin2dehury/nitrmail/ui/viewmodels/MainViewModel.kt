package github.sachin2dehury.nitrmail.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import github.sachin2dehury.nitrmail.api.data.entities.Mail
import github.sachin2dehury.nitrmail.others.Event
import github.sachin2dehury.nitrmail.others.Resource
import github.sachin2dehury.nitrmail.repository.MainRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(
    private val repository: MainRepository
) : ViewModel() {

/*
val mails = appClient.mails

fun postCredential(userRoll: String, userPassword: String) {
val result = Credentials.basic(userRoll, userPassword)
appClient.setCredentials(result)
}
private val _mails = MutableLiveData<Mails>()
val mails: LiveData<Mails> = _mails

private val _item = MutableLiveData<ParsedMail>()
val item: LiveData<ParsedMail> = _item

private val _credential = MutableLiveData<String>()
val credential: LiveData<String> = _credential
*/

    private val _mails = MutableLiveData<Event<Resource<Mail>>>()
    val mails: LiveData<Event<Resource<Mail>>> = _mails

    fun insertMail(mail: Mail) = GlobalScope.launch {
        repository.insertMail(mail)
    }

//    fun getNoteById(noteID: String) = viewModelScope.launch {
//        _mails.postValue(Event(Resource.loading(null)))
//        val note = repository.getNoteById(noteID)
//        note?.let {
//            _mails.postValue(Event(Resource.success(it)))
//        } ?: _mails.postValue(Event(Resource.error("Note not found", null)))
//    }
}