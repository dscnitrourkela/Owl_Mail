package github.sachin2dehury.owlmail.ui.viewmodels

import android.text.format.DateUtils
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.others.Event
import github.sachin2dehury.owlmail.repository.DataStoreRepository
import github.sachin2dehury.owlmail.repository.MailRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MailBoxViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val mailRepository: MailRepository,
) : ViewModel() {

    private val _request = MutableLiveData<String>()

    private val lastSync = _request.switchMap { request ->
        dataStoreRepository.readLastSync(Constants.KEY_LAST_SYNC + request)
            .map { it ?: Constants.NO_LAST_SYNC }.asLiveData(viewModelScope.coroutineContext)
    }

    val mails = lastSync.switchMap { lastSync ->
        mailRepository.getMails(_request.value!!, lastSync)
            .asLiveData(viewModelScope.coroutineContext)
    }.switchMap {
        MutableLiveData(Event(it))
    }

    fun saveLastSync() = viewModelScope.launch {
        dataStoreRepository.saveLastSync(
            Constants.KEY_LAST_SYNC + _request.value,
            System.currentTimeMillis() - DateUtils.MINUTE_IN_MILLIS
        )
    }

    fun syncAllMails(request: String) = _request.postValue(request)
}