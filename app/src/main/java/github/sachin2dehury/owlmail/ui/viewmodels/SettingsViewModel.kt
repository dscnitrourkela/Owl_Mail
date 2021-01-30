package github.sachin2dehury.owlmail.ui.viewmodels

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.repository.DataStoreRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
) : ViewModel() {

    private val _forceUpdate = MutableLiveData(false)

    val isDarkThemeEnabled = _forceUpdate.switchMap {
        dataStoreRepository.readState(Constants.KEY_DARK_THEME)
            .asLiveData(viewModelScope.coroutineContext).map { it ?: true }
    }

    val shouldSync = _forceUpdate.switchMap {
        dataStoreRepository.readState(Constants.KEY_SHOULD_SYNC)
            .asLiveData(viewModelScope.coroutineContext).map { it ?: false }
    }

    fun refreshStates() = _forceUpdate.postValue(true)

    fun saveThemeState(isDarkThemeEnabled: Boolean) = viewModelScope.launch {
        dataStoreRepository.saveState(Constants.KEY_DARK_THEME, isDarkThemeEnabled)
    }

    fun saveSyncState(shouldSync: Boolean) = viewModelScope.launch {
        dataStoreRepository.saveState(Constants.KEY_SHOULD_SYNC, shouldSync)
    }
}