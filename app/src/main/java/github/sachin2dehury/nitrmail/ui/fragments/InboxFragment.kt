package github.sachin2dehury.nitrmail.ui.fragments

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.nitrmail.ui.viewmodels.InboxViewModel

@AndroidEntryPoint
class InboxFragment : MailBoxFragment() {
    override val viewModel: InboxViewModel by viewModels()
}