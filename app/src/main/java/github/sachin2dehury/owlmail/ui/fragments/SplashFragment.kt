package github.sachin2dehury.owlmail.ui.fragments

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.owlmail.NavGraphDirections
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.ui.ActivityExt
import github.sachin2dehury.owlmail.ui.enableActionBar
import github.sachin2dehury.owlmail.ui.viewmodels.SplashViewModel

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {

    private val viewModel: SplashViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeToObservers()

        (requireActivity() as AppCompatActivity).enableActionBar(false)
        (requireActivity() as ActivityExt).enableDrawer(false)

        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    private fun subscribeToObservers() {
        viewModel.isLoggedIn.observe(viewLifecycleOwner, {
            it?.let {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.splashFragment, true)
                    .build()
                when (it) {
                    true -> findNavController().navigate(
                        NavGraphDirections.actionToMailBoxFragment(Constants.INBOX_URL),
                        navOptions
                    )
                    false -> findNavController().navigate(
                        NavGraphDirections.actionToAuthFragment(),
                        navOptions
                    )
                }
            }
        })
    }
}