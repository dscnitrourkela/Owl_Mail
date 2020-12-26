package github.sachin2dehury.nitrmail.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.nitrmail.R
import github.sachin2dehury.nitrmail.databinding.FragmentMailItemBinding
import github.sachin2dehury.nitrmail.others.Constants
import github.sachin2dehury.nitrmail.others.Status
import github.sachin2dehury.nitrmail.ui.DrawerExt
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

    private val args: MailItemFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MailItemViewModel.id = args.id

        (activity as DrawerExt).setDrawerEnabled(false)

        _binding = FragmentMailItemBinding.bind(view)

        viewModel = ViewModelProvider(requireActivity()).get(MailItemViewModel::class.java)

        viewModel.syncParsedMails()
        subscribeToObservers()
    }

    @SuppressLint("SimpleDateFormat", "SetJavaScriptEnabled")
    private fun subscribeToObservers() {
        viewModel.parsedMail.observe(viewLifecycleOwner, {
            it?.let { event ->
                val result = event.peekContent()
                result.data?.let { mail ->
                    binding.apply {
                        progressBarMail.isVisible = false
                        textViewDate.text =
                            SimpleDateFormat(Constants.DATE_FORMAT_YEAR).format(mail.date)
                        textViewMailSubject.text = mail.subject
                        textViewSender.text = mail.from.email
                        webView.apply {
                            settings.javaScriptEnabled = true
                            settings.loadWithOverviewMode = true
                            settings.defaultTextEncodingName = "utf-8"
                            settings.loadsImagesAutomatically = true
                            settings.domStorageEnabled = true
                            settings.useWideViewPort = false
                            isHorizontalScrollBarEnabled = false
                            val body = mail.bodyText + mail.bodyHtml
                            val html = HtmlCompat.fromHtml(
                                body, HtmlCompat.FROM_HTML_MODE_LEGACY
                            ).toString()
                            loadData(html, "text/html", "utf-8")
                        }
                    }
                }
                when (result.status) {
                    Status.SUCCESS -> {
                        binding.progressBarMail.isVisible = false
                    }
                    Status.ERROR -> {
                        event.getContentIfNotHandled()?.let { errorResource ->
                            errorResource.message?.let { message ->
                                showSnackbar(message)
                            }
                        }
                        binding.progressBarMail.isVisible = false
                    }
                    Status.LOADING -> {
                        binding.progressBarMail.isVisible = true
                    }
                }
            }
        })
    }

    private fun showSnackbar(text: String) {
        Snackbar.make(binding.root, text, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}