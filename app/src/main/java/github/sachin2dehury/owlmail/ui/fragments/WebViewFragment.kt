package github.sachin2dehury.owlmail.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.databinding.FragmentWebViewBinding
import github.sachin2dehury.owlmail.ui.ActivityExt
import javax.inject.Inject

@AndroidEntryPoint
open class WebViewFragment : Fragment(R.layout.fragment_web_view) {

    private var _binding: FragmentWebViewBinding? = null
    private val binding: FragmentWebViewBinding get() = _binding!!

    private val args: WebViewFragmentArgs by navArgs()

    @Inject
    lateinit var css: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentWebViewBinding.bind(view)

        requireActivity().apply {
//            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            (this as AppCompatActivity).supportActionBar?.let {
                it.title = args.request.substringBefore('.')
                it.show()
            }
            (this as ActivityExt).enableDrawer(false)
        }

        val assets = requireContext().assets
        val page = css + assets.open(args.request).bufferedReader().use { it.readText() }

        binding.webView.loadDataWithBaseURL(null, page, "text/html", "utf-8", null)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}