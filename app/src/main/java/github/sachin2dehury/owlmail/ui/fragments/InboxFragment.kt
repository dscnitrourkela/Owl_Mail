package github.sachin2dehury.owlmail.ui.fragments

import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.owlmail.others.Constants

@AndroidEntryPoint
class InboxFragment : MailBoxFragment(Constants.INBOX_URL)