package github.sachin2dehury.nitrmail.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.sachin2dehury.nitrmail.api.data.entities.Mail
import github.sachin2dehury.nitrmail.others.Resource
import github.sachin2dehury.nitrmail.repository.MainRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private val _request = MutableLiveData<String>()
    val request: LiveData<String> = _request

    private val _mails = MutableLiveData<Resource<List<Mail>>>()
    val mails: LiveData<Resource<List<Mail>>> = _mails

    fun insertMails() = GlobalScope.launch {
        val result = mails.value?.data
        result.let {
            if (it != null) {
                repository.insertMails(it)
            }
        }
    }

    fun getMails() {
        _mails.postValue(Resource.loading(null))
        viewModelScope.launch {
            val result = repository.getMailsNetwork(request.value!!)
            _mails.postValue(result)
        }
    }

    fun setRequest(string: String) {
        _request.postValue(string)
    }
}