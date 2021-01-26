package github.sachin2dehury.owlmail.ui.fragments

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.owlmail.NavGraphDirections
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.databinding.FragmentAuthBinding
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.others.Status
import github.sachin2dehury.owlmail.ui.ActivityExt
import github.sachin2dehury.owlmail.ui.viewmodels.AuthViewModel
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

        viewModel.syncState()

        _binding = FragmentAuthBinding.bind(view)

        binding.buttonLogin.setOnClickListener {
//            AlertDialog.Builder(requireContext()).apply {
//                setMessage("Loading...")
//                setCancelable(false)
//            }.show()
            getCredential()
            viewModel.login(credential)
            (requireActivity() as ActivityExt).hideKeyBoard()
        }

//        binding.swipeRefreshLayout.setOnRefreshListener {
//            viewModel.login(credential)
//        }

        (requireActivity() as ActivityExt).apply {
            toggleActionBar(false)
            toggleDrawer(false)
        }

        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        subscribeToObservers()
    }

    private fun redirectFragment() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.authFragment, true)
            .build()
        findNavController().navigate(
            NavGraphDirections.actionToInboxFragment(),
            navOptions
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
                        (requireActivity() as ActivityExt).showSnackbar("Successfully logged in")
                        viewModel.saveLogIn()
                        redirectFragment()
                    }
                    Status.ERROR -> {
                        (requireActivity() as ActivityExt).showSnackbar(
                            it.message ?: "An unknown error occurred"
                        )
                    }
                    Status.LOADING -> {
                    }
                }
            }
        })
        viewModel.isLoggedIn.observe(viewLifecycleOwner, {
            when (it) {
                true -> redirectFragment()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}