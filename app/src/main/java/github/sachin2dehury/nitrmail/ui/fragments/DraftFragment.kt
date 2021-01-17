package github.sachin2dehury.nitrmail.ui.fragments

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.nitrmail.ui.viewmodels.DraftViewModel

@AndroidEntryPoint
class DraftFragment : MailBoxFragment() {
    override val viewModel: DraftViewModel by viewModels()
}