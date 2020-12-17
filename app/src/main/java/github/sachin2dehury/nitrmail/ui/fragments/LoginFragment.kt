package github.sachin2dehury.nitrmail.ui.fragments

import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.nitrmail.R
import github.sachin2dehury.nitrmail.api.calls.BasicAuthInterceptor
import github.sachin2dehury.nitrmail.databinding.FragmentLoginBinding
import github.sachin2dehury.nitrmail.others.Constants
import github.sachin2dehury.nitrmail.others.Status
import github.sachin2dehury.nitrmail.ui.viewmodels.AuthViewModel
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val viewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var sharedPref: SharedPreferences

    @Inject
    lateinit var basicAuthInterceptor: BasicAuthInterceptor

    private var curRoll: String? = null
    private var curPassword: String? = null

    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isLoggedIn()) {
            authenticate(curRoll ?: "", curPassword ?: "")
        }
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        subscribeToObservers()

        _binding = FragmentLoginBinding.bind(view)

        binding.buttonLogin.setOnClickListener {
            val roll = binding.editTextUserRoll.text.toString()
            val password = binding.editTextUserPassword.text.toString()
            curRoll = roll
            curPassword = password
            viewModel.login(roll, password)
        }
    }

    private fun isLoggedIn(): Boolean {
        curRoll = sharedPref.getString(Constants.KEY_LOGGED_IN_EMAIL, Constants.NO_EMAIL)
            ?: Constants.NO_EMAIL
        curPassword = sharedPref.getString(Constants.KEY_PASSWORD, Constants.NO_PASSWORD)
            ?: Constants.NO_PASSWORD
        return curRoll != Constants.NO_EMAIL && curPassword != Constants.NO_PASSWORD
    }

    private fun authenticate(email: String, password: String) {
        basicAuthInterceptor.email = email
        basicAuthInterceptor.password = password
    }

    private fun subscribeToObservers() {
        viewModel.loginStatus.observe(viewLifecycleOwner, Observer { result ->
            result?.let {
                when (result.status) {
                    Status.SUCCESS -> {
                        binding.progressBar.visibility = View.GONE
                        showSnackbar(result.data ?: "Successfully logged in")
                        sharedPref.edit().putString(Constants.KEY_LOGGED_IN_EMAIL, curRoll).apply()
                        sharedPref.edit().putString(Constants.KEY_PASSWORD, curPassword).apply()
                        authenticate(curRoll ?: "", curPassword ?: "")
                        Timber.d("CALLED")
                    }
                    Status.ERROR -> {
                        binding.progressBar.visibility = View.GONE
                        showSnackbar(result.message ?: "An unknown error occured")
                    }
                    Status.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun showSnackbar(text: String) {
        Snackbar.make(binding.root, text, Snackbar.LENGTH_LONG).show()
    }
}