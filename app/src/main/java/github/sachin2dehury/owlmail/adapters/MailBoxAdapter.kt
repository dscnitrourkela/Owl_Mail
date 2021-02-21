package github.sachin2dehury.owlmail.adapters

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.api.data.Mail
import github.sachin2dehury.owlmail.databinding.MailItemBinding
import github.sachin2dehury.owlmail.others.Constants
import java.text.SimpleDateFormat

class MailBoxAdapter(private val colors: IntArray) :
    PagingDataAdapter<Mail, MailBoxAdapter.MailBoxViewHolder>(

        object : DiffUtil.ItemCallback<Mail>() {

            override fun areItemsTheSame(oldItem: Mail, newItem: Mail) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Mail, newItem: Mail) =
                oldItem.hashCode() == newItem.hashCode()
        }) {

    private var onItemClickListener: ((Mail) -> Unit)? = null

    private val colorsLength = colors.lastIndex

    class MailBoxViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MailBoxViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.mail_item, parent, false)
        )

    override fun onBindViewHolder(holderBox: MailBoxViewHolder, position: Int) {
        getItem(position)?.let { mail ->
            val binding = MailItemBinding.bind(holderBox.itemView)
            val sender =
                if (mail.flag?.contains('s') == true) mail.addresses.first() else mail.addresses.last()

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

            val color = colors[mail.size % colorsLength]

            binding.apply {
                textViewSender.text = sender.firstName.first().toString()
                textViewSender.background.setTint(color)
                textViewDate.text = dateFormat.format(mail.time)
                textViewMailBody.text = mail.body
                textViewMailSubject.text = mail.subject
                textViewSenderEmail.text = sender.firstName
                if (mail.flag?.contains('u') == true) {
                    textViewSenderEmail.typeface = Typeface.DEFAULT_BOLD
                    textViewMailSubject.typeface = Typeface.DEFAULT_BOLD
                    textViewDate.typeface = Typeface.DEFAULT_BOLD
                    textViewMailBody.typeface = Typeface.DEFAULT_BOLD
                }
                if (mail.flag?.contains('f') == true) {
                    imageViewStared.isVisible = true
                }
                if (mail.flag?.contains('a') == true) {
                    imageViewAttachment.isVisible = true
                }
//            if (mail.flag.contains('r')) {
//                imageViewReply.isVisible = true
//            }
                if (position == itemCount - 1) {
                    divider.isVisible = false
                }
            }
            holderBox.itemView.setOnClickListener {
                onItemClickListener?.let { onClick -> onClick(mail) }
            }
        }
    }

    fun setupOnItemClickListener(onClick: ((Mail) -> Unit)) {
        onItemClickListener = onClick
    }
}