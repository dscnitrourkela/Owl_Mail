package github.sachin2dehury.owlmail.ui.fragments

import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.owlmail.ui.viewmodels.TrashViewModel

@AndroidEntryPoint
class TrashFragment : MailBoxFragment() {
    override val viewModel: TrashViewModel by activityViewModels()
}