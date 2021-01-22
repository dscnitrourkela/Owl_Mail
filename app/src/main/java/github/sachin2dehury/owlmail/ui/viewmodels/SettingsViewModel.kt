package github.sachin2dehury.owlmail.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel @ViewModelInject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _forceUpdate = MutableLiveData(false)

    val isDarkThemeEnabled = _forceUpdate.switchMap {
        repository.readState(Constants.KEY_DARK_THEME)
            .asLiveData(viewModelScope.coroutineContext).map {
                it ?: true
            }
    }

    val shouldSync = _forceUpdate.switchMap {
        repository.readState(Constants.KEY_SHOULD_SYNC)
            .asLiveData(viewModelScope.coroutineContext).map {
                it ?: false
            }
    }

    fun readStates() = _forceUpdate.postValue(true)

    fun saveThemeState(isDarkThemeEnabled: Boolean) = viewModelScope.launch {
        repository.saveState(Constants.KEY_DARK_THEME, isDarkThemeEnabled)
    }

    fun saveSyncState(shouldSync: Boolean) = viewModelScope.launch {
        repository.saveState(Constants.KEY_SHOULD_SYNC, shouldSync)
    }

    fun logout() = CoroutineScope(Dispatchers.IO).launch { repository.logout() }

//    fun getUserRoll() = repository.getUser()
}