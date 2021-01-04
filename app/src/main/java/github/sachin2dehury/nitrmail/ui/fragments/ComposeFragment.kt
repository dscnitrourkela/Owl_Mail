package github.sachin2dehury.nitrmail.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.nitrmail.R
import github.sachin2dehury.nitrmail.databinding.FragmentComposeBinding
import github.sachin2dehury.nitrmail.others.Constants
import github.sachin2dehury.nitrmail.ui.ActivityExt
import github.sachin2dehury.nitrmail.ui.viewmodels.ComposeViewModel

@AndroidEntryPoint
class ComposeFragment : Fragment(R.layout.fragment_compose) {

    private var _binding: FragmentComposeBinding? = null
    private val binding: FragmentComposeBinding get() = _binding!!

    private val viewModel: ComposeViewModel by viewModels()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentComposeBinding.bind(view)

        (activity as ActivityExt).apply {
            toggleDrawer(false)
            toggleActionBar(true)
        }

        val header = mapOf("Cookie" to viewModel.token)

        binding.webView.apply {
            settings.javaScriptEnabled = true
            settings.loadsImagesAutomatically = true
            loadUrl(Constants.HOME_URL + Constants.COMPOSE_URL, header)
        }
    }
}