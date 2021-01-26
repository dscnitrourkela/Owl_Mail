package github.sachin2dehury.owlmail.ui.fragments

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.owlmail.ui.viewmodels.SentViewModel

@AndroidEntryPoint
class SentFragment : MailBoxFragment() {
    override val viewModel: SentViewModel by viewModels()
}