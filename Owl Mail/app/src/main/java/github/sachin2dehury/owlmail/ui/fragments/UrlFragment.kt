package github.sachin2dehury.owlmail.ui.fragments

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.webkit.URLUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.owlmail.NavGraphDirections
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.databinding.FragmentUrlBinding
import github.sachin2dehury.owlmail.utils.hideKeyBoard
import github.sachin2dehury.owlmail.utils.showSnackbar

@AndroidEntryPoint
class UrlFragment : Fragment(R.layout.fragment_url) {

    private var _binding: FragmentUrlBinding? = null
    private val binding: FragmentUrlBinding get() = _binding!!

    private var url: String? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentUrlBinding.bind(view)

        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        binding.buttonGo.setOnClickListener {
            binding.editTextUrl.text?.let { url ->
                this.url = url.toString()
                if (URLUtil.isValidUrl(this.url)) {
                    binding.root.hideKeyBoard()
                    findNavController().navigate(NavGraphDirections.actionToAuthFragment(this.url!!))
                } else {
                    it?.showSnackbar("Invalid Url!")
                }
            }
        }

        binding.buttonPrivacyPolicy.setOnClickListener {
            findNavController().navigate(NavGraphDirections.actionToWebViewFragment(getString(R.string.privacy_policy)))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}