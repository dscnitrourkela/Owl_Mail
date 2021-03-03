package github.sachin2dehury.owlmail.ui.viewmodels

import androidx.core.os.persistableBundleOf
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import github.sachin2dehury.owlmail.R
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
        dataStoreRepository.readState(R.string.key_dark_theme)
            .asLiveData(viewModelScope.coroutineContext)
    }

    val shouldSync = _forceUpdate.switchMap {
        dataStoreRepository.readState(R.string.key_should_sync)
            .asLiveData(viewModelScope.coroutineContext)
    }

    fun saveThemeState(isDarkThemeEnabled: Boolean) = viewModelScope.launch {
        dataStoreRepository.saveState(R.string.key_dark_theme, isDarkThemeEnabled)
    }

    fun saveSyncState(shouldSync: Boolean) = viewModelScope.launch {
        dataStoreRepository.saveState(R.string.key_should_sync, shouldSync)
    }

    fun syncState() = _forceUpdate.postValue(true)

    fun getBundle() = persistableBundleOf(
//        ApiConstants.KEY_SHOULD_SYNC to shouldSync.value,
//        ApiConstants.KEY_SYNC_SERVICE to System.currentTimeMillis(),
//        ApiConstants.KEY_TOKEN to mailRepository.getToken(),
//        ApiConstants.KEY_CREDENTIAL to mailRepository.getCredential()
    )
}