package github.sachin2dehury.owlmail.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.api.data.Mail
import github.sachin2dehury.owlmail.others.ApiConstants
import github.sachin2dehury.owlmail.others.Resource
import github.sachin2dehury.owlmail.repository.DataStoreRepository
import github.sachin2dehury.owlmail.repository.MailRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
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
            saveCredential(R.string.key_credential, mailRepository.getCredential())
            saveCredential(R.string.key_token, mailRepository.getToken())
            ApiConstants.BASE_URL?.let { saveCredential(R.string.key_url, it) }
            saveState(R.string.key_should_sync, true)
        }
    }

    fun resetLogin() = viewModelScope.launch {
        dataStoreRepository.resetLogin()
        mailRepository.resetLogin()
    }
}