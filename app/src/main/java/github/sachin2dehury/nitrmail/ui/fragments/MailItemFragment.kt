package github.sachin2dehury.nitrmail.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.nitrmail.R
import github.sachin2dehury.nitrmail.databinding.FragmentMailItemBinding
import github.sachin2dehury.nitrmail.others.Constants
import github.sachin2dehury.nitrmail.others.Status
import github.sachin2dehury.nitrmail.ui.ActivityExt
import github.sachin2dehury.nitrmail.ui.viewmodels.MailItemViewModel
import java.text.SimpleDateFormat

@AndroidEntryPoint
class MailItemFragment : Fragment(R.layout.fragment_mail_item) {

    private var _binding: FragmentMailItemBinding? = null
    private val binding: FragmentMailItemBinding get() = _binding!!

    private val viewModel: MailItemViewModel by viewModels()

    private val args: MailItemFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentMailItemBinding.bind(view)

        (activity as ActivityExt).apply {
            toggleDrawer(false)
            toggleActionBar(true)
        }

        subscribeToObservers()

        viewModel.setId(args.id, args.hasAttachments)

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.syncParsedMails()
        }
    }

    @SuppressLint("SimpleDateFormat", "SetJavaScriptEnabled")
    private fun subscribeToObservers() {
        viewModel.parsedMail.observe(viewLifecycleOwner, {
            it?.let { event ->
                val result = event.peekContent()
                result.data?.let { mail ->
                    val sender =
                        if (mail.flag.contains('s')) mail.addresses.first() else mail.addresses.last()
                    binding.apply {
                        textViewDate.text =
                            SimpleDateFormat(Constants.DATE_FORMAT_YEAR).format(mail.time)
                        textViewMailSubject.text = mail.subject
                        textViewSenderName.text =
                            if (sender.name.isNotEmpty()) sender.name else sender.email.substringBefore(
                                '@'
                            )
                        textViewSenderEmail.text = sender.email
                        if (mail.flag.contains('a')) {
                            imageViewAttachment.isVisible = true
                        }
                        webView.apply {
                            settings.javaScriptEnabled = true
                            settings.loadsImagesAutomatically = true
                            setInitialScale(160)
                            loadDataWithBaseURL(
                                Constants.BASE_URL,
                                mail.html,
                                "text/html",
                                "utf-8",
                                null
                            )
                        }
                    }
                }
                when (result.status) {
                    Status.SUCCESS -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    Status.ERROR -> {
                        event.getContentIfNotHandled()?.let { errorResource ->
                            errorResource.message?.let { message ->
                                (activity as ActivityExt).showSnackbar(message)
                            }
                        }
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    Status.LOADING -> {
                        binding.swipeRefreshLayout.isRefreshing = true
                    }
                }
            }
        })
        viewModel.id.observe(viewLifecycleOwner, {
            viewModel.syncParsedMails()
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}