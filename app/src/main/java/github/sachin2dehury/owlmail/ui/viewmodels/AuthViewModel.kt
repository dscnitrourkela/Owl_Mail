package github.sachin2dehury.owlmail.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
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

    private val _forceUpdate = MutableLiveData(false)

    private val _loginStatus = MutableLiveData<Resource<List<Mail>>>()
    val loginStatus: LiveData<Resource<List<Mail>>> = _loginStatus

    val isLoggedIn = _forceUpdate.switchMap {
        dataStoreRepository.readCredential(Constants.KEY_TOKEN)
            .asLiveData(viewModelScope.coroutineContext)
            .map { token ->
                if (token != null && token != Constants.NO_TOKEN) {
                    mailRepository.setToken(token)
                    true
                } else {
                    false
                }
            }.switchMap { loggedIn ->
                dataStoreRepository.readCredential(Constants.KEY_CREDENTIAL).asLiveData()
                    .map { credential ->
                        if (credential != null && credential != Constants.NO_TOKEN) {
                            mailRepository.setCredential(credential)
                            true
                        } else {
                            loggedIn
                        }
                    }
            }
    }

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

    fun syncState() = _forceUpdate.postValue(true)
}