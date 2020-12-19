package github.sachin2dehury.nitrmail.ui.viewmodels

import androidx.lifecycle.ViewModel
import github.sachin2dehury.nitrmail.repository.MainRepository
import javax.inject.Inject

class MailItemViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel()