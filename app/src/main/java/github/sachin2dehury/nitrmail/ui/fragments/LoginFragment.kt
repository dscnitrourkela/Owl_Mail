package github.sachin2dehury.nitrmail.ui.fragments

import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.nitrmail.R
import github.sachin2dehury.nitrmail.api.calls.BasicAuthInterceptor
import github.sachin2dehury.nitrmail.databinding.FragmentLoginBinding
import github.sachin2dehury.nitrmail.others.Constants
import github.sachin2dehury.nitrmail.others.Status
import github.sachin2dehury.nitrmail.ui.viewmodels.AuthViewModel
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val viewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var sharedPref: SharedPreferences

    @Inject
    lateinit var basicAuthInterceptor: BasicAuthInterceptor

    lateinit var roll: String
    lateinit var password: String

    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isLoggedIn()) {
            authenticate()
        }

        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        subscribeToObservers()

        _binding = FragmentLoginBinding.bind(view)

        binding.buttonLogin.setOnClickListener {
            roll = binding.editTextUserRoll.text.toString()
            password = binding.editTextUserPassword.text.toString()

            authenticate()
        }
    }

    private fun isLoggedIn(): Boolean {
        roll = sharedPref.getString(Constants.KEY_LOGGED_IN_EMAIL, Constants.NO_EMAIL)
            ?: Constants.NO_EMAIL
        password = sharedPref.getString(Constants.KEY_PASSWORD, Constants.NO_PASSWORD)
            ?: Constants.NO_PASSWORD
        return roll != Constants.NO_EMAIL && password != Constants.NO_PASSWORD
    }

    private fun authenticate() {
        basicAuthInterceptor.roll = roll
        basicAuthInterceptor.password = password

        viewModel.login(roll, password)
    }

    private fun subscribeToObservers() {
        viewModel.loginStatus.observe(viewLifecycleOwner, { result ->
            result?.let {
                when (result.status) {
                    Status.SUCCESS -> {
                        binding.progressBar.visibility = View.GONE
                        showSnackbar("Successfully logged in")
                        sharedPref.edit().putString(Constants.KEY_LOGGED_IN_EMAIL, roll).apply()
                        sharedPref.edit().putString(Constants.KEY_PASSWORD, password).apply()
                        findNavController().navigate(R.id.action_loginFragment_to_mailBoxFragment)
                    }
                    Status.ERROR -> {
                        binding.progressBar.visibility = View.GONE
                        showSnackbar("An unknown error occured")
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