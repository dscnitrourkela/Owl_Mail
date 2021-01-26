package github.sachin2dehury.owlmail.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.repository.DataStoreRepository
import github.sachin2dehury.owlmail.repository.MailRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel @ViewModelInject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val mailRepository: MailRepository,
) : ViewModel() {

    private val _forceUpdate = MutableLiveData(false)

    val isDarkThemeEnabled = _forceUpdate.switchMap {
        dataStoreRepository.readState(Constants.KEY_DARK_THEME)
            .asLiveData(viewModelScope.coroutineContext).map {
                it ?: true
            }
    }

    val shouldSync = _forceUpdate.switchMap {
        dataStoreRepository.readState(Constants.KEY_SHOULD_SYNC)
            .asLiveData(viewModelScope.coroutineContext).map {
                it ?: false
            }
    }

    fun refreshStates() = _forceUpdate.postValue(true)

    fun saveThemeState(isDarkThemeEnabled: Boolean) = viewModelScope.launch {
        dataStoreRepository.saveState(Constants.KEY_DARK_THEME, isDarkThemeEnabled)
    }

    fun saveSyncState(shouldSync: Boolean) = viewModelScope.launch {
        dataStoreRepository.saveState(Constants.KEY_SHOULD_SYNC, shouldSync)
    }

    fun logout() = CoroutineScope(Dispatchers.IO).launch {
        mailRepository.resetLogin()
        dataStoreRepository.logout()
    }

//    fun getUserRoll() = repository.getUser()
}