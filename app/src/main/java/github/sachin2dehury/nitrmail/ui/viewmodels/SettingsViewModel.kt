package github.sachin2dehury.nitrmail.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import github.sachin2dehury.nitrmail.repository.Repository
import kotlinx.coroutines.launch

class SettingsViewModel @ViewModelInject constructor(
    private val repository: Repository
) : ThemeViewModel(repository) {

    fun logout() = viewModelScope.launch { repository.logOut() }

    fun saveRefreshTime() {}

    fun readRefreshTime() {}
}