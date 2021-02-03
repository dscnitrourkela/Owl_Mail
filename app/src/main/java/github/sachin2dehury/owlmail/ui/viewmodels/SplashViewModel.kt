package github.sachin2dehury.owlmail.ui.viewmodels

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import github.sachin2dehury.owlmail.others.Constants
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
        dataStoreRepository.readCredential(Constants.KEY_TOKEN)
            .asLiveData(viewModelScope.coroutineContext).map { token ->
                if (token != null && token != Constants.NO_TOKEN) {
                    mailRepository.setToken(token)
                    true
                } else {
                    false
                }
            }.switchMap { loggedIn ->
                dataStoreRepository.readCredential(Constants.KEY_CREDENTIAL)
                    .asLiveData(viewModelScope.coroutineContext).map { credential ->
                        if (credential != null && credential != Constants.NO_TOKEN) {
                            mailRepository.setCredential(credential)
                            true
                        } else {
                            loggedIn
                        }
                    }
            }
    }

    fun syncState() = _forceUpdate.postValue(true)
}