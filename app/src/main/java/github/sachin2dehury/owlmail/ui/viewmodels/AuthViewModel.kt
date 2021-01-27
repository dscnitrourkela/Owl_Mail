package github.sachin2dehury.owlmail.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.sachin2dehury.owlmail.api.data.Mail
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.others.Resource
import github.sachin2dehury.owlmail.repository.DataStoreRepository
import github.sachin2dehury.owlmail.repository.MailRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel @ViewModelInject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val mailRepository: MailRepository,
) : ViewModel() {

    private val _loginStatus = MutableLiveData<Resource<List<Mail>>>()
    val loginStatus: LiveData<Resource<List<Mail>>> = _loginStatus

    fun login(credential: String) {
        _loginStatus.postValue(Resource.loading(null))
        if (credential.isEmpty()) {
            _loginStatus.postValue(Resource.error("Please fill out all the fields", null))
            return
        }
        mailRepository.setCredential(credential)
        CoroutineScope(viewModelScope.coroutineContext).launch {
            val result = mailRepository.login()
            _loginStatus.postValue(result)
        }
    }

    fun saveLogIn() = CoroutineScope(Dispatchers.IO).launch {
        dataStoreRepository.apply {
            saveCredential(Constants.KEY_CREDENTIAL, mailRepository.getCredential())
            saveCredential(Constants.KEY_TOKEN, mailRepository.getToken())
            saveState(Constants.KEY_SHOULD_SYNC, true)
        }
    }
}