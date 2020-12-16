package github.sachin2dehury.nitrmail.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import github.sachin2dehury.nitrmail.repository.MainRepository

class MailItemViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository
) : ViewModel()