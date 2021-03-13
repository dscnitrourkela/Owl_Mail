package github.sachin2dehury.owlmail.ui.viewmodels

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.others.ApiConstants
import github.sachin2dehury.owlmail.repository.DataStoreRepository
import github.sachin2dehury.owlmail.repository.MailRepository
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val mailRepository: MailRepository,
) : ViewModel() {

    private val _forceUpdate = MutableLiveData(false)

    val isLoggedIn = _forceUpdate.switchMap {
        dataStoreRepository.readCredential(R.string.key_url)
            .asLiveData(viewModelScope.coroutineContext).map {
                it?.let { ApiConstants.BASE_URL = it }
            }
        dataStoreRepository.readCredential(R.string.key_token)
            .asLiveData(viewModelScope.coroutineContext).map { token ->
                if (token.isNullOrEmpty()) {
                    false
                } else {
                    mailRepository.setToken(token)
                    true
                }
            }.switchMap { loggedIn ->
                dataStoreRepository.readCredential(R.string.key_credential)
                    .asLiveData(viewModelScope.coroutineContext).map { credential ->
                        if (credential.isNullOrEmpty()) {
                            loggedIn
                        } else {
                            mailRepository.setCredential(credential)
                            true
                        }
                    }
            }
    }

    fun syncState() = _forceUpdate.postValue(true)
}