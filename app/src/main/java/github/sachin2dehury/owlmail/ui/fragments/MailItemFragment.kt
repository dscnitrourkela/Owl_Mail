package github.sachin2dehury.owlmail.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.api.calls.MailViewClient
import github.sachin2dehury.owlmail.api.data.Mail
import github.sachin2dehury.owlmail.databinding.FragmentMailItemBinding
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.others.Resource
import github.sachin2dehury.owlmail.others.Status
import github.sachin2dehury.owlmail.ui.ActivityExt
import github.sachin2dehury.owlmail.ui.enableActionBar
import github.sachin2dehury.owlmail.ui.showSnackbar
import github.sachin2dehury.owlmail.ui.viewmodels.MailItemViewModel
import java.text.SimpleDateFormat
import javax.inject.Inject

@AndroidEntryPoint
class MailItemFragment : Fragment(R.layout.fragment_mail_item) {

    private var _binding: FragmentMailItemBinding? = null
    private val binding: FragmentMailItemBinding get() = _binding!!

    private val args: MailItemFragmentArgs by navArgs()

    private val viewModel: MailItemViewModel by viewModels()

//    @Inject
//    lateinit var colors: IntArray
//    private var colorsLength: Int = 0

    @Inject
    lateinit var mailViewClient: MailViewClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setId(args.id)

//        colorsLength = colors.size

        _binding = FragmentMailItemBinding.bind(view)

        (requireActivity() as AppCompatActivity).enableActionBar(true)
        (requireActivity() as ActivityExt).enableDrawer(false)

        setupWebView()

        subscribeToObservers()

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.setId(args.id)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        binding.webView.apply {
            isVerticalScrollBarEnabled = false
            settings.javaScriptEnabled = true
            settings.loadsImagesAutomatically = true
            setInitialScale(160)
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                val darkMode = when (AppCompatDelegate.getDefaultNightMode()) {
                    AppCompatDelegate.MODE_NIGHT_NO -> WebSettingsCompat.FORCE_DARK_OFF
                    else -> WebSettingsCompat.FORCE_DARK_ON
                }
                WebSettingsCompat.setForceDark(this.settings, darkMode)
            }
        }
    }

    private fun subscribeToObservers() {
        viewModel.parsedMail.observe(viewLifecycleOwner, {
            it?.let { event ->
                val result = event.peekContent()
                when (result.status) {
                    Status.SUCCESS -> {
                        setContent(result)
                        binding.swipeRefreshLayout.isRefreshing = false
                        binding.webView.isVisible = true
                    }
                    Status.ERROR -> {
                        event.getContentIfNotHandled()?.let { errorResource ->
                            errorResource.message?.let { message ->
                                view?.showSnackbar(message)
                            }
                        }
                        setContent(result)
                        binding.swipeRefreshLayout.isRefreshing = false
                        binding.webView.isVisible = true
                    }
                    Status.LOADING -> {
                        setContent(result)
                        binding.swipeRefreshLayout.isRefreshing = true
                        binding.webView.isVisible = false
                    }
                }
            }
        })
    }

    @SuppressLint("SimpleDateFormat")
    private fun setContent(result: Resource<Mail>) {
        result.data?.let { mail ->
            val sender =
                if (mail.flag.contains('s')) mail.addresses.first() else mail.addresses.last()
            val dateFormat = SimpleDateFormat(Constants.DATE_FORMAT_FULL)
            val name =
                if (sender.name.isNotEmpty()) sender.name else sender.email.substringBefore(
                    '@'
                )
            var senderName = "From : "
            var receiverName = "To : "
            mail.addresses.forEach {
                if (it.isReceiver.contains('f', true)) {
                    senderName = senderName + it.name + "<${it.email}>\n"
                }
                receiverName = receiverName + it.name + "<${it.email}>\n"
            }
            val date = dateFormat.format(mail.time)
            val newValue = "$senderName$receiverName$date"
//            val color = colors[mail.size % colorsLength]
            val color = when (sender.email.contains("@nitrkl.ac.in", true)) {
                true -> {
                    try {
                        sender.email.first().toInt()
                        R.color.rally_green_300
                    } catch (e: Exception) {
                        R.color.rally_green_500
                    }
                }
                else -> R.color.rally_green_700
            }
            binding.apply {
                textViewSender.text = name.first().toString()
                textViewSender.background.setTint(color)
                textViewDate.text = date
                textViewMailSubject.text = mail.subject
                textViewSenderName.text =
                    if (sender.name.isNotEmpty()) sender.name else sender.email.substringBefore(
                        '@'
                    )
                textViewReceiverEmail.text = sender.email
                if (mail.flag.contains('a')) {
                    imageViewAttachment.isVisible = true
                }
                if (mail.body.isEmpty()) {
                    view?.showSnackbar("This mail has no content")
                }
                webView.loadDataWithBaseURL(
                    Constants.BASE_URL,
                    mail.parsedBody,
                    "text/html",
                    "utf-8",
                    null
                )
                textInputLayout.addOnEndIconChangedListener { _, previousIcon ->
                    textViewDate.isVisible = false
                    textViewReceiverEmail.text = newValue
//                        textViewDate.isVisible = true
//                        textViewReceiverEmail.text = sender.email
//
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}