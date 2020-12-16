package github.sachin2dehury.nitrmail.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.sachin2dehury.nitrmail.others.Resource
import github.sachin2dehury.nitrmail.repository.MainRepository
import kotlinx.coroutines.launch

class AuthViewModel @ViewModelInject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private val _loginStatus = MutableLiveData<Resource<String>>()
    val loginStatus: LiveData<Resource<String>> = _loginStatus

    fun login(email: String, password: String) {
        _loginStatus.postValue(Resource.loading(null))
        if (email.isEmpty() || password.isEmpty()) {
            _loginStatus.postValue(Resource.error("Please fill out all the fields", null))
            return
        }
        viewModelScope.launch {
            val result = repository.login(email, password)
            _loginStatus.postValue(result)
        }
    }
}