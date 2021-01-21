package github.sachin2dehury.owlmail.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import github.sachin2dehury.owlmail.api.calls.BasicAuthInterceptor
import github.sachin2dehury.owlmail.api.data.Mail
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.others.Resource
import github.sachin2dehury.owlmail.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel @ViewModelInject constructor(
    private val repository: Repository,
    private val basicAuthInterceptor: BasicAuthInterceptor
) : ViewModel() {

    private val _loginStatus = MutableLiveData<Resource<List<Mail>>>()
    val loginStatus: LiveData<Resource<List<Mail>>> = _loginStatus

    val isLoggedIn = _loginStatus.switchMap {
        repository.readCredential(Constants.KEY_TOKEN).asLiveData(viewModelScope.coroutineContext)
            .map { token ->
                if (token != null && token != Constants.NO_TOKEN) {
                    basicAuthInterceptor.token = token
                    true
                } else {
                    false
                }
            }.switchMap { loggedIn ->
                repository.readCredential(Constants.KEY_CREDENTIAL).asLiveData().map { credential ->
                    if (credential != null && credential != Constants.NO_TOKEN) {
                        basicAuthInterceptor.credential = credential
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
        CoroutineScope(Dispatchers.IO).launch {
            val result = repository.login(credential)
            _loginStatus.postValue(result)
        }
    }

    fun saveLogIn() = CoroutineScope(Dispatchers.IO).launch {
        repository.saveCredential(Constants.KEY_CREDENTIAL, basicAuthInterceptor.credential)
        repository.saveCredential(Constants.KEY_TOKEN, basicAuthInterceptor.token)
        repository.saveState(Constants.KEY_SHOULD_SYNC, true)
    }
}