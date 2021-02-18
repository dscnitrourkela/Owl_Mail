package github.sachin2dehury.owlmail.ui.viewmodels

import androidx.core.os.persistableBundleOf
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.repository.DataStoreRepository
import github.sachin2dehury.owlmail.repository.MailRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val mailRepository: MailRepository,
) : ViewModel() {

    private val _forceUpdate = MutableLiveData(false)

    val isDarkThemeEnabled = _forceUpdate.switchMap {
        dataStoreRepository.readState(Constants.KEY_DARK_THEME)
            .asLiveData(viewModelScope.coroutineContext)
    }

    val shouldSync = _forceUpdate.switchMap {
        dataStoreRepository.readState(Constants.KEY_SHOULD_SYNC)
            .asLiveData(viewModelScope.coroutineContext)
    }

    fun saveThemeState(isDarkThemeEnabled: Boolean) = viewModelScope.launch {
        dataStoreRepository.saveState(Constants.KEY_DARK_THEME, isDarkThemeEnabled)
    }

    fun saveSyncState(shouldSync: Boolean) = viewModelScope.launch {
        dataStoreRepository.saveState(Constants.KEY_SHOULD_SYNC, shouldSync)
    }

    fun syncState() = _forceUpdate.postValue(true)

    fun getBundle() = persistableBundleOf(
        Constants.KEY_SHOULD_SYNC to shouldSync.value,
        Constants.KEY_SYNC_SERVICE to System.currentTimeMillis(),
        Constants.KEY_TOKEN to mailRepository.getToken(),
        Constants.KEY_CREDENTIAL to mailRepository.getCredential()
    )
}