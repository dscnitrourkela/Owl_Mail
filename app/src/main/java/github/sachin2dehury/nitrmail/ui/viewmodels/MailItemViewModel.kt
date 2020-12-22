package github.sachin2dehury.nitrmail.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import github.sachin2dehury.nitrmail.api.data.Mail
import github.sachin2dehury.nitrmail.repository.MainRepository

class MailItemViewModel @ViewModelInject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private val _item = MutableLiveData<Mail>()
    val item: LiveData<Mail> = _item
}