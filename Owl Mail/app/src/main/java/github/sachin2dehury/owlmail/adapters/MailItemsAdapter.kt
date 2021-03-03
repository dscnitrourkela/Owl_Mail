package github.sachin2dehury.owlmail.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.api.data.ParsedMail
import github.sachin2dehury.owlmail.databinding.MailItemsBinding
import github.sachin2dehury.owlmail.others.ApiConstants
import github.sachin2dehury.owlmail.utils.showToast

class MailItemsAdapter(
    private val context: Context,
    private val colors: IntArray,
    private val attachmentAdapter: AttachmentAdapter,
) : PagingDataAdapter<ParsedMail, MailItemsAdapter.MailItemsViewHolder>(

    object : DiffUtil.ItemCallback<ParsedMail>() {

        override fun areItemsTheSame(oldItem: ParsedMail, newItem: ParsedMail) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ParsedMail, newItem: ParsedMail) =
            oldItem.hashCode() == newItem.hashCode()
    }) {

    private val colorsLength = colors.lastIndex

    class MailItemsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    var id = 0
    var token: String? = null
    var css: String? = null
    var view: TextView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MailItemsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.mail_items, parent, false)
        )

    override fun onBindViewHolder(holder: MailItemsViewHolder, position: Int) {
        val binding = MailItemsBinding.bind(holder.itemView)
        getItem(position)?.let { mail ->
            if (position == itemCount - 1) {
                binding.divider.isVisible = false
            }
            view?.text = mail.subject
            setupWebView(binding)
            setContent(binding, mail)
            holder.itemView.setOnClickListener {
                binding.apply {
                    webView.isVisible = !webView.isVisible
                    recyclerViewAttachments.isVisible = webView.isVisible
                    textViewEmailDetails.isVisible =
                        webView.isVisible && textViewMailBody.isVisible
                    textViewMailBody.setOnClickListener {
                        textViewEmailDetails.isVisible = !textViewEmailDetails.isVisible
                    }
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setContent(binding: MailItemsBinding, mail: ParsedMail) {
        val color = colors[mail.id % colorsLength]
        val sender = mail.from?.split(' ')
        val body = mail.body?.replace("auth=co", "auth=qp&amp;zauthtoken=$token") + css
        var address = ""
        val mailBody = "${sender?.last()}\n${mail.time}"
        mail.address?.forEach {
            address += it
        }
        binding.apply {
            textViewSenderName.text = sender?.first()
            textViewSender.text = sender?.first()?.first().toString()
            textViewSender.background.setTint(color)
            textViewMailBody.text = mailBody
            textViewEmailDetails.text = address
//            if (mail.flag?.contains('f') == true) {
//                imageViewStared.isVisible = true
//            }
            if (mail.attachmentsName?.isNullOrEmpty() == true) {
                imageViewAttachment.isVisible = true
            }
//            if (mail.flag?.contains('r') == true) {
//                imageViewReply.isVisible = true
//            }
            webView.loadDataWithBaseURL(ApiConstants.BASE_URL, body, "text/html", "utf-8", null)
            recyclerViewAttachments.apply {
                attachmentAdapter.attachmentsName = mail.attachmentsName
                attachmentAdapter.attachmentsLink = mail.attachmentsLink ?: emptyList()
                adapter = attachmentAdapter
                layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }
            if (mail.id == id) {
                recyclerViewAttachments.isVisible = true
                if (mail.body.isNullOrEmpty()) {
                    root.showToast("This mail has no content")
                }
            } else {
                webView.isVisible = false
            }
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(binding: MailItemsBinding) {
        binding.webView.apply {
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                val darkMode = when (AppCompatDelegate.getDefaultNightMode()) {
                    AppCompatDelegate.MODE_NIGHT_YES -> WebSettingsCompat.FORCE_DARK_ON
                    else -> WebSettingsCompat.FORCE_DARK_OFF
                }
                WebSettingsCompat.setForceDark(this.settings, darkMode)
            }
            isVerticalScrollBarEnabled = false
            settings.javaScriptEnabled = true
            settings.loadsImagesAutomatically = true
        }
    }

    fun setupOnItemClickListener(onClick: ((String) -> Unit)) {
        attachmentAdapter.onItemClickListener = onClick
    }
}