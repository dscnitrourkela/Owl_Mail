package github.sachin2dehury.owlmail.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.repository.DataStoreRepository
import github.sachin2dehury.owlmail.repository.MailRepository

class InboxViewModel @ViewModelInject constructor(
    dataStoreRepository: DataStoreRepository,
    mailRepository: MailRepository
) :
    MailBoxViewModel(dataStoreRepository, mailRepository) {
    override val request = Constants.INBOX_URL
}