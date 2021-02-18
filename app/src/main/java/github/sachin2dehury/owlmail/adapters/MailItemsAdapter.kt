package github.sachin2dehury.owlmail.adapters

import android.annotation.SuppressLint
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.api.data.Mail
import github.sachin2dehury.owlmail.databinding.MailItemsBinding
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.others.debugLog
import github.sachin2dehury.owlmail.ui.showToast
import org.jsoup.Jsoup
import java.text.SimpleDateFormat

class MailItemsAdapter(
    private val colors: IntArray,
    private val attachmentAdapter: AttachmentAdapter
) : MailAdapter(R.layout.mail_items) {

    private val colorsLength = colors.lastIndex

    var id = 0
    var token: String? = null
    var css: String? = null

    override fun onBindViewHolder(holder: MailViewHolder, position: Int) {
        val binding = MailItemsBinding.bind(holder.itemView)
        val mail = mails[position]
        if (position == mails.lastIndex) {
            binding.divider.isVisible = false
        }
        setContent(binding, mail)
        holder.itemView.setOnClickListener {
            binding.webView.isVisible = !binding.webView.isVisible
            binding.recyclerViewAttachments.isVisible = binding.webView.isVisible
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(binding: MailItemsBinding) {
        binding.webView.apply {
            isVerticalScrollBarEnabled = false
            settings.javaScriptEnabled = true
            settings.loadsImagesAutomatically = true
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                val darkMode = when (AppCompatDelegate.getDefaultNightMode()) {
                    AppCompatDelegate.MODE_NIGHT_YES -> WebSettingsCompat.FORCE_DARK_ON
                    else -> WebSettingsCompat.FORCE_DARK_OFF
                }
                WebSettingsCompat.setForceDark(this.settings, darkMode)
            }
        }
    }

    private fun toggleView(binding: MailItemsBinding, mail: Mail) {
        val sender =
            if (mail.flag?.contains('s') == true) mail.addresses.first() else mail.addresses.last()
        when (binding.webView.isVisible) {
            true -> binding.apply {
                val text = sender.firstName + "\n" + sender.email
                var receiver = ""
                mail.addresses.forEach {
                    if (it.isReceiver) {
                        receiver += it.firstName + "\n" + it.email
                    }
                }
                debugLog(receiver)
                textViewSenderName.maxLines = 2
                textViewSenderName.text = text
                textViewMailBody.text = receiver
                textViewMailBody.maxLines = 100
            }
            else -> binding.apply {
                textViewSenderName.maxLines = 1
                textViewMailBody.maxLines = 2
                textViewSenderName.text = sender.firstName
                textViewMailBody.text = mail.body
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setContent(binding: MailItemsBinding, mail: Mail) {
        val sender =
            if (mail.flag?.contains('s') == true) mail.addresses.first() else mail.addresses.last()
        val color = colors[mail.size % colorsLength]
        val dateFormat = SimpleDateFormat(Constants.DATE_FORMAT_FULL)

        binding.apply {
            textViewDate.text = dateFormat.format(mail.time)
            textViewSenderName.text = sender.firstName
            textViewSender.text = sender.firstName.first().toString()
            textViewSender.background.setTint(color)
            textViewMailBody.text = mail.body
        }

        setupWebView(binding)
        getMailDetails(mail, binding)

        if (mail.id == id) {
            binding.apply {
                if (mail.flag?.contains('a') == true) {
                    imageViewAttachment.isVisible = true
                }
                webView.isVisible = true
                recyclerViewAttachments.isVisible = true
                val text = sender.email + "\nto me"
                textViewMailBody.text = text
                if (mail.body.isNullOrEmpty()) {
                    root.showToast("This mail has no content")
                }
            }
        } else {
            binding.apply {
                if (mail.flag?.contains('u') == true) {
                    textViewSenderName.typeface = Typeface.DEFAULT_BOLD
                    textViewDate.typeface = Typeface.DEFAULT_BOLD
                    textViewMailBody.typeface = Typeface.DEFAULT_BOLD
                }
                if (mail.flag?.contains('f') == true) {
                    imageViewStared.isVisible = true
                }
                if (mail.flag?.contains('a') == true) {
                    imageViewAttachment.isVisible = true
                }
//            if (mail.flag?.contains('r') == true) {
//                imageViewReply.isVisible = true
//            }
            }
        }
    }

    private fun getMailDetails(mail: Mail, binding: MailItemsBinding) {
        val parsedMail = Jsoup.parse(mail.parsedBody ?: "")
        val attachments = parsedMail.select(".View.attachments").text().trim()
        val list = if (attachments.isEmpty()) emptyList() else attachments.split(") ")
        val cc = parsedMail.select("#d_div").text().substringAfter("Cc:").trim()
        val bcc = parsedMail.select("#d_div").text().substringAfter("Bcc:").trim()
        val quota = parsedMail.select(".quota-span").text()
        val body = parsedMail.select("#iframeBody").text()
            .replace("auth=co", "auth=qp&amp;zauthtoken=$token") + css
        debugLog(body)
        binding.webView.loadDataWithBaseURL(
            Constants.BASE_URL,
            body,
            "text/html",
            "utf-8",
            null
        )
        binding.recyclerViewAttachments.apply {
            attachmentAdapter.list = list
            adapter = attachmentAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    fun setupOnItemClickListener(onClick: ((Int) -> Unit)) {
        attachmentAdapter.onItemClickListener = onClick
    }
}