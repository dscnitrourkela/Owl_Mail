package github.sachin2dehury.nitrmail.ui.viewmodels

import androidx.lifecycle.ViewModel
import github.sachin2dehury.nitrmail.repository.MainRepository
import javax.inject.Inject

class MailItemViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

//    fun getMailItem() = viewModelScope.launch {
//        _mail.postValue(Event(Resource.loading(null)))
//        val mail = repository.getMailItem()
//        mail?.let {
//            _mail.postValue(Event(Resource.success(it)))
//        } ?: _mail.postValue(Event(Resource.error("Note not found", null)))
//    }
}