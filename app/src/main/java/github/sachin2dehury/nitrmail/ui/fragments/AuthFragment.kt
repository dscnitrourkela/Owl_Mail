package github.sachin2dehury.nitrmail.ui.fragments

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.nitrmail.R
import github.sachin2dehury.nitrmail.databinding.FragmentAuthBinding
import github.sachin2dehury.nitrmail.others.Constants
import github.sachin2dehury.nitrmail.others.Status
import github.sachin2dehury.nitrmail.services.SyncService
import github.sachin2dehury.nitrmail.ui.ActivityExt
import github.sachin2dehury.nitrmail.ui.viewmodels.AuthViewModel
import kotlinx.coroutines.launch
import okhttp3.Credentials

@AndroidEntryPoint
class AuthFragment : Fragment(R.layout.fragment_auth) {

    private var _binding: FragmentAuthBinding? = null
    private val binding: FragmentAuthBinding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()

    private var credential = Constants.NO_CREDENTIAL

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as ActivityExt).apply {
            toggleDrawer(false)
            toggleActionBar(false)
        }

        isLoggedIn()

        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT


        subscribeToObservers()

        _binding = FragmentAuthBinding.bind(view)

        binding.buttonLogin.setOnClickListener {
            getCredential()
            viewModel.login(credential)
        }
    }

    private fun isLoggedIn() = lifecycleScope.launch {
        if (viewModel.isLoggedIn()) {
            findNavController().navigate(R.id.action_authFragment_to_mailBoxFragment)
            val intent = Intent(requireContext(), SyncService::class.java).apply {
                putExtra(Constants.KEY_LAST_SYNC, Constants.NO_LAST_SYNC)
            }
            requireContext().startService(intent)
        }
    }

    private fun getCredential() {
        val roll = binding.editTextUserRoll.text.toString()
        val password = binding.editTextUserPassword.text.toString()
        credential = Credentials.basic(roll, password)
    }

    private fun subscribeToObservers() {
        viewModel.loginStatus.observe(viewLifecycleOwner, { result ->
            result?.let {
                when (result.status) {
                    Status.SUCCESS -> {
                        lifecycleScope.launch {
                            viewModel.saveLogInCredential(credential)
                        }
                        binding.swipeRefreshLayout.isRefreshing = false
                        (activity as ActivityExt).showSnackbar("Successfully logged in")
                        findNavController().navigate(R.id.action_authFragment_to_mailBoxFragment)
                    }
                    Status.ERROR -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                        (activity as ActivityExt).showSnackbar(
                            it.message ?: "An unknown error occurred"
                        )
                    }
                    Status.LOADING -> {
                        binding.swipeRefreshLayout.isRefreshing = true
                    }
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}