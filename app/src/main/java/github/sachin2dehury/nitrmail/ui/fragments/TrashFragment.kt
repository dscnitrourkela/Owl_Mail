package github.sachin2dehury.nitrmail.ui.fragments

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.nitrmail.ui.viewmodels.TrashViewModel

@AndroidEntryPoint
class TrashFragment : MailBoxFragment() {
    override val viewModel: TrashViewModel by viewModels()
}