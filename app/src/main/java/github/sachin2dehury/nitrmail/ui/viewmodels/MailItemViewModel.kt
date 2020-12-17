package github.sachin2dehury.nitrmail.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import github.sachin2dehury.nitrmail.api.data.entities.Mail
import github.sachin2dehury.nitrmail.others.Event
import github.sachin2dehury.nitrmail.others.Resource
import github.sachin2dehury.nitrmail.repository.MainRepository
import javax.inject.Inject

class MailItemViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private val _mail = MutableLiveData<Event<Resource<Mail>>>()
    val mail: LiveData<Event<Resource<Mail>>> = _mail

//    fun getMailItem() = viewModelScope.launch {
//        _mail.postValue(Event(Resource.loading(null)))
//        val mail = repository.getMailItem()
//        mail?.let {
//            _mail.postValue(Event(Resource.success(it)))
//        } ?: _mail.postValue(Event(Resource.error("Note not found", null)))
//    }
}