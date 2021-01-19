package github.sachin2dehury.owlmail.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.repository.Repository

class SentViewModel @ViewModelInject constructor(
    repository: Repository,
) :
    MailBoxViewModel(repository) {
    override val request = Constants.SENT_URL
}