package github.sachin2dehury.owlmail.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.databinding.FragmentWebViewBinding
import github.sachin2dehury.owlmail.others.debugLog

@AndroidEntryPoint
open class WebViewFragment : Fragment(R.layout.fragment_web_view) {

    private var _binding: FragmentWebViewBinding? = null
    private val binding: FragmentWebViewBinding get() = _binding!!

    private val args: WebViewFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentWebViewBinding.bind(view)

        setContent()

        binding.swipeRefreshLayout.setOnRefreshListener {
            setContent()
        }
    }

    private fun setContent() = try {
        val assets = requireContext().assets
        val css = assets.open("Font").bufferedReader().use { it.readText() }
        val page = assets.open(args.request).bufferedReader().use { it.readText() }
        binding.webView.loadDataWithBaseURL(null, page + css, "text/html", "utf-8", null)
    } catch (e: Exception) {
        debugLog(e.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}