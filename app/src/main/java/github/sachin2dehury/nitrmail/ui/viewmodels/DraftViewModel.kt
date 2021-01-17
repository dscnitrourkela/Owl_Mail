package github.sachin2dehury.nitrmail.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import github.sachin2dehury.nitrmail.others.Constants
import github.sachin2dehury.nitrmail.repository.Repository

class DraftViewModel @ViewModelInject constructor(
    repository: Repository,
) :
    MailBoxViewModel(repository) {
    override val request = Constants.DRAFT_URL
}