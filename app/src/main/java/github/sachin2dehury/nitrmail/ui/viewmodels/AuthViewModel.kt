package github.sachin2dehury.nitrmail.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.sachin2dehury.nitrmail.api.data.Mail
import github.sachin2dehury.nitrmail.others.Resource
import github.sachin2dehury.nitrmail.repository.MainRepository
import kotlinx.coroutines.launch

class AuthViewModel @ViewModelInject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private val _loginStatus = MutableLiveData<Resource<List<Mail>>>()
    val loginStatus: LiveData<Resource<List<Mail>>> = _loginStatus

    fun login(credential: String) {
        _loginStatus.postValue(Resource.loading(null))
        if (credential.isEmpty()) {
            _loginStatus.postValue(Resource.error("Please fill out all the fields", null))
            return
        }
        viewModelScope.launch {
            val result = repository.login()
            _loginStatus.postValue(result)
        }
    }
}