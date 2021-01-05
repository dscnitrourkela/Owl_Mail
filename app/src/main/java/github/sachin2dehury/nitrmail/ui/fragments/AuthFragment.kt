package github.sachin2dehury.nitrmail.ui.fragments

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.nitrmail.R
import github.sachin2dehury.nitrmail.databinding.FragmentAuthBinding
import github.sachin2dehury.nitrmail.others.Constants
import github.sachin2dehury.nitrmail.others.Status
import github.sachin2dehury.nitrmail.ui.ActivityExt
import github.sachin2dehury.nitrmail.ui.viewmodels.AuthViewModel
import kotlinx.coroutines.launch
import okhttp3.Credentials
import java.util.*

@AndroidEntryPoint
class AuthFragment : Fragment(R.layout.fragment_auth) {

    private var _binding: FragmentAuthBinding? = null
    private val binding: FragmentAuthBinding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()

    private var credential = Constants.NO_CREDENTIAL

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentAuthBinding.bind(view)

        binding.swipeRefreshLayout.isVisible = false

        binding.buttonLogin.setOnClickListener {
            getCredential()
            viewModel.login(credential)
            (activity as ActivityExt).hideKeyBoard()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.login(credential)
        }

        isLoggedIn()

        (activity as ActivityExt).apply {
            inAppUpdate()
            toggleActionBar(false)
            toggleDrawer(false)
            inAppReview()
        }

        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        subscribeToObservers()
    }

    private fun isLoggedIn() = lifecycleScope.launch {
        if (viewModel.isLoggedIn()) {
            redirectFragment()
        } else {
            binding.swipeRefreshLayout.isVisible = true
            showKeyBoard()
        }
    }

    private fun redirectFragment() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.authFragment, true)
            .build()
        findNavController().navigate(
            AuthFragmentDirections.actionAuthFragmentToMailBoxFragment(),
            navOptions
        )
    }

    private fun showKeyBoard() {
        (requireActivity().getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(
            binding.editTextUserRoll,
            0
        )
    }

    private fun getCredential() {
        val roll = binding.editTextUserRoll.text.toString().toLowerCase(Locale.ROOT)
        val password = binding.editTextUserPassword.text.toString()
        credential = Credentials.basic(roll, password)
    }

    private fun subscribeToObservers() {
        viewModel.loginStatus.observe(viewLifecycleOwner, { result ->
            result?.let {
                when (result.status) {
                    Status.SUCCESS -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                        (activity as ActivityExt).showSnackbar("Successfully logged in")
                        redirectFragment()
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