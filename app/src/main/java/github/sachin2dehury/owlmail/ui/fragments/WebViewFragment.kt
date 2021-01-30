package github.sachin2dehury.owlmail.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.databinding.FragmentWebViewBinding
import github.sachin2dehury.owlmail.ui.ActivityExt
import github.sachin2dehury.owlmail.ui.enableActionBar
import github.sachin2dehury.owlmail.ui.openAsset

@AndroidEntryPoint
open class WebViewFragment(private val fileName: String) : Fragment(R.layout.fragment_web_view) {

    private var _binding: FragmentWebViewBinding? = null
    private val binding: FragmentWebViewBinding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentWebViewBinding.bind(view)

        (requireActivity() as AppCompatActivity).enableActionBar(true)
        (requireActivity() as ActivityExt).enableDrawer(false)

        val page = (requireActivity() as AppCompatActivity).openAsset(fileName)
        binding.textView.text = HtmlCompat.fromHtml(page, HtmlCompat.FROM_HTML_MODE_LEGACY)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}