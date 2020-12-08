package github.sachin2dehury.nitrmail.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.nitrmail.R
import github.sachin2dehury.nitrmail.databinding.FragmentLoginBinding
import github.sachin2dehury.nitrmail.ui.viewmodels.MainViewModel

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    lateinit var mainViewModel: MainViewModel

    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentLoginBinding.bind(view)

        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        binding.buttonLogin.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val roll = binding.textViewUserRoll.text.toString()
        val password = binding.textViewUserPassword.text.toString()
        mainViewModel.postCredential(roll, password)
        findNavController().navigate(R.id.action_loginFragment_to_mailBoxFragment)
    }
}