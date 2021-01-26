package github.sachin2dehury.owlmail.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.databinding.FragmentAboutBinding
import github.sachin2dehury.owlmail.ui.ActivityExt

@AndroidEntryPoint
class AboutFragment : Fragment(R.layout.fragment_about) {

    private var _binding: FragmentAboutBinding? = null
    private val binding: FragmentAboutBinding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentAboutBinding.bind(view)

        (activity as ActivityExt).apply {
            toggleDrawer(false)
            toggleActionBar(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}