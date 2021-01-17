package github.sachin2dehury.nitrmail.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.sachin2dehury.nitrmail.others.Constants
import github.sachin2dehury.nitrmail.repository.Repository
import kotlinx.coroutines.launch

open class ThemeViewModel @ViewModelInject constructor(
    private val repository: Repository
) : ViewModel() {

    private var _themeState = MutableLiveData(Constants.DARK_THEME)
    val themeState: LiveData<String> = _themeState

    fun setThemeState(themeState: String) = _themeState.postValue(themeState)

    fun saveThemeState() = viewModelScope.launch { repository.saveThemeState(themeState.value!!) }

    fun readThemeState() =
        viewModelScope.launch { _themeState.postValue(repository.readThemeState()) }
}