package github.sachin2dehury.nitrmail.ui.fragments

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.nitrmail.ui.viewmodels.SentViewModel

@AndroidEntryPoint
class SentFragment : MailBoxFragment() {
    override val viewModel: SentViewModel by viewModels()
}