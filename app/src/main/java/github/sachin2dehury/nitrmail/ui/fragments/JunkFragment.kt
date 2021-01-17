package github.sachin2dehury.nitrmail.ui.fragments

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.nitrmail.ui.viewmodels.JunkViewModel

@AndroidEntryPoint
class JunkFragment : MailBoxFragment() {
    override val viewModel: JunkViewModel by viewModels()
}