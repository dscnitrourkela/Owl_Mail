package github.sachin2dehury.nitrmail.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.nitrmail.R
import github.sachin2dehury.nitrmail.databinding.FragmentMailItemBinding
import github.sachin2dehury.nitrmail.ui.viewmodels.MailItemViewModel
import java.text.SimpleDateFormat
import javax.inject.Inject

@AndroidEntryPoint
class MailItemFragment : Fragment(R.layout.fragment_mail_item) {

    lateinit var viewModel: MailItemViewModel

    private var _binding: FragmentMailItemBinding? = null
    private val binding: FragmentMailItemBinding get() = _binding!!

    @Inject
    lateinit var dateFormat: SimpleDateFormat

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentMailItemBinding.bind(view)

        viewModel = ViewModelProvider(requireActivity()).get(MailItemViewModel::class.java)

        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        viewModel.item.observe(viewLifecycleOwner) { result ->
            result?.let {
                binding.apply {
                    progressBarMail.isVisible = false
                    viewModel.item.value?.let { mail ->
                        textViewDate.text = dateFormat.format(mail.time)
                        textViewMailSubject.text = mail.subject
                        textViewSender.text = mail.senders.last().email
//                        webView.apply {
//                            settings.loadsImagesAutomatically = true
//                            loadData("${mail.bodyHtml} ${mail.bodyHtml}", "text/html", "UTF-8")
//                        }
//                        textViewMailBody.text = mail.bodyText
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}