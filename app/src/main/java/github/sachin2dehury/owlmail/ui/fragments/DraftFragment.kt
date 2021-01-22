package github.sachin2dehury.owlmail.ui.fragments

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.owlmail.ui.viewmodels.DraftViewModel

@AndroidEntryPoint
class DraftFragment : MailBoxFragment() {
    override val viewModel: DraftViewModel by viewModels()
}