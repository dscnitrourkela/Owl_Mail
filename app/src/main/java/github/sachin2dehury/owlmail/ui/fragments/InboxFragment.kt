package github.sachin2dehury.owlmail.ui.fragments

import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.owlmail.ui.viewmodels.InboxViewModel

@AndroidEntryPoint
class InboxFragment : MailBoxFragment() {
    override val viewModel: InboxViewModel by activityViewModels()
}