package github.sachin2dehury.owlmail.adapters

import android.annotation.SuppressLint
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.api.calls.MailViewClient
import github.sachin2dehury.owlmail.api.data.Mail
import github.sachin2dehury.owlmail.databinding.MailItemsBinding
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.ui.showToast
import java.text.SimpleDateFormat

class MailItemsAdapter(private val colors: IntArray, private val mailViewClient: MailViewClient) :
    MailAdapter(R.layout.mail_items) {

    private val colorsLength = colors.lastIndex

    var id = ""

    override fun onBindViewHolder(holder: MailViewHolder, position: Int) {
        val binding = MailItemsBinding.bind(holder.itemView)
        val mail = mails[position]
        if (position == mails.lastIndex) {
            binding.divider.isVisible = false
        }
        setContent(binding, mail)
        holder.itemView.setOnClickListener {
            binding.webView.isVisible = !binding.webView.isVisible
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(binding: MailItemsBinding) {
        binding.webView.apply {
            webViewClient = mailViewClient
            isVerticalScrollBarEnabled = false
            settings.javaScriptEnabled = true
            settings.loadsImagesAutomatically = true
            settings.setSupportZoom(true)
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                val darkMode = when (AppCompatDelegate.getDefaultNightMode()) {
                    AppCompatDelegate.MODE_NIGHT_NO -> WebSettingsCompat.FORCE_DARK_OFF
                    else -> WebSettingsCompat.FORCE_DARK_ON
                }
                WebSettingsCompat.setForceDark(this.settings, darkMode)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setContent(binding: MailItemsBinding, mail: Mail) {
        val sender = if (mail.flag.contains('s')) mail.addresses.first() else mail.addresses.last()
        val name = if (sender.name.isNotEmpty()) sender.name else sender.email.substringBefore('@')
        val color = colors[mail.size % colorsLength]
        val dateFormat = SimpleDateFormat(Constants.DATE_FORMAT_FULL)

        binding.apply {
            textViewDate.text = dateFormat.format(mail.time)
            textViewSenderName.text = name
            textViewSender.text = name.first().toString()
            textViewSender.background.setTint(color)
            textViewMailBody.text = mail.body
            setupWebView(binding)
            webView.loadDataWithBaseURL(
                Constants.BASE_URL,
                mail.parsedBody,
                "text/html",
                "utf-8",
                null
            )
        }

        if (mail.id == id) {
            binding.apply {
                if (mail.flag.contains('a')) {
                    imageViewAttachment.isVisible = true
                }
                webView.isVisible = true
                if (mail.body.isEmpty()) {
                    root.showToast("This mail has no content")
                }
            }
        } else {
            binding.apply {
                if (mail.flag.contains('u')) {
                    textViewSenderName.typeface = Typeface.DEFAULT_BOLD
                    textViewDate.typeface = Typeface.DEFAULT_BOLD
                    textViewMailBody.typeface = Typeface.DEFAULT_BOLD
                }
                if (mail.flag.contains('f')) {
                    imageViewStared.isVisible = true
                }
                if (mail.flag.contains('a')) {
                    imageViewAttachment.isVisible = true
                }
//            if (mail.flag.contains('r')) {
//                imageViewReply.isVisible = true
//            }
            }
        }
    }
}