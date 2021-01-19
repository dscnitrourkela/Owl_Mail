package github.sachin2dehury.owlmail.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import github.sachin2dehury.owlmail.repository.Repository

class AboutViewModel @ViewModelInject constructor(
    private val repository: Repository
) : ViewModel()