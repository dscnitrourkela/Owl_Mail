package github.sachin2dehury.owlmail.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.api.calls.MailViewClient
import github.sachin2dehury.owlmail.databinding.FragmentComposeBinding
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.ui.ActivityExt
import github.sachin2dehury.owlmail.ui.viewmodels.ComposeViewModel
import javax.inject.Inject

@AndroidEntryPoint
class ComposeFragment : Fragment(R.layout.fragment_compose) {

    private var _binding: FragmentComposeBinding? = null
    private val binding: FragmentComposeBinding get() = _binding!!

    private val viewModel: ComposeViewModel by viewModels()

    private val activityExt = requireActivity() as ActivityExt

    @Inject
    lateinit var mailViewClient: MailViewClient

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentComposeBinding.bind(view)

        activityExt.toggleDrawer(false)

        val url =
            "${Constants.HOME_URL + Constants.COMPOSE_URL}&auth=qp&zauthtoken=${viewModel.token}"

        binding.webView.apply {
            webViewClient = mailViewClient
            settings.javaScriptEnabled = true
            settings.loadsImagesAutomatically = true
            loadUrl(url)
        }
    }
}