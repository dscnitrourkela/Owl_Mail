package github.sachin2dehury.owlmail.adapters

import android.annotation.SuppressLint
import android.graphics.Typeface
import androidx.core.view.isVisible
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.api.data.Mail
import github.sachin2dehury.owlmail.databinding.MailItemBinding
import github.sachin2dehury.owlmail.others.Constants
import java.text.SimpleDateFormat

class MailBoxAdapter(private val colors: IntArray) : MailAdapter(R.layout.mail_item) {

    private var onItemClickListener: ((Mail) -> Unit)? = null

    private val colorsLength = colors.lastIndex

    override fun onBindViewHolder(holder: MailViewHolder, position: Int) {
        val mail = mails[position]
        val binding = MailItemBinding.bind(holder.itemView)
        val sender =
            if (mail.flag.contains('s')) mail.addresses.first() else mail.addresses.last()

        @SuppressLint("SimpleDateFormat")
        val dateFormat = when {
            (System.currentTimeMillis() - mail.time) < Constants.DAY -> {
                SimpleDateFormat(Constants.DATE_FORMAT_DATE)
            }
            (System.currentTimeMillis() - mail.time) < Constants.YEAR -> {
                SimpleDateFormat(Constants.DATE_FORMAT_MONTH)
            }
            else -> {
                SimpleDateFormat(Constants.DATE_FORMAT_YEAR)
            }
        }
        val name = if (sender.name.isNotEmpty()) sender.name else sender.email.substringBefore('@')
        val color = colors[mail.size % colorsLength]
//        val color = when (sender.email.contains("@nitrkl.ac.in", true)) {
//            true -> {
//                try {
//                    sender.email.first().toInt()
//                    colors.first()
//                } catch (e: Exception) {
//                    colors[1]
//                }
//            }
//            else -> colors.last()
//        }
        binding.apply {
            textViewSender.text = name.first().toString()
            textViewSender.background.setTint(color)
            textViewDate.text = dateFormat.format(mail.time)
            textViewMailBody.text = mail.body
            textViewMailSubject.text = mail.subject
            textViewSenderEmail.text = name
            if (mail.flag.contains('u')) {
                textViewSenderEmail.typeface = Typeface.DEFAULT_BOLD
                textViewMailSubject.typeface = Typeface.DEFAULT_BOLD
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
            if (position == mails.lastIndex) {
                divider.isVisible = false
            }
        }
        holder.itemView.setOnClickListener {
            onItemClickListener?.let { onClick -> onClick(mail) }
        }
    }

    fun setupOnItemClickListener(onClick: ((Mail) -> Unit)) {
        onItemClickListener = onClick
    }
}