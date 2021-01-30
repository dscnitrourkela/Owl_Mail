package github.sachin2dehury.owlmail.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.api.calls.MailViewClient
import github.sachin2dehury.owlmail.databinding.FragmentWebViewBinding
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.ui.ActivityExt
import github.sachin2dehury.owlmail.ui.enableActionBar
import github.sachin2dehury.owlmail.ui.viewmodels.ComposeViewModel
import javax.inject.Inject

@AndroidEntryPoint
class ComposeFragment : Fragment(R.layout.fragment_web_view) {

    private var _binding: FragmentWebViewBinding? = null
    private val binding: FragmentWebViewBinding get() = _binding!!

    private val viewModel: ComposeViewModel by viewModels()

    @Inject
    lateinit var mailViewClient: MailViewClient

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentWebViewBinding.bind(view)

        (requireActivity() as AppCompatActivity).enableActionBar(true)
        (requireActivity() as ActivityExt).enableDrawer(false)

        val url =
//            "https://mail.nitrkl.ac.in/h/?action=compose"
            Constants.BASE_URL + Constants.MOBILE_URL + Constants.AUTH_FROM_COOKIE + Constants.COMPOSE_MAIL

        binding.webView.apply {
            webViewClient = mailViewClient
            settings.javaScriptEnabled = true
            settings.loadsImagesAutomatically = true
            loadUrl(url, mapOf("Cookie" to viewModel.token))
        }
    }
}