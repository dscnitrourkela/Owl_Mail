package github.sachin2dehury.owlmail.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.api.data.Mail
import github.sachin2dehury.owlmail.databinding.MailItemBinding
import github.sachin2dehury.owlmail.others.Constants
import java.text.SimpleDateFormat

class MailItemsAdapter(context: Context) :
    RecyclerView.Adapter<MailItemsAdapter.MailItemViewHolder>() {

    class MailItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val diffCallback = object : DiffUtil.ItemCallback<Mail>() {

        override fun areItemsTheSame(oldItem: Mail, newItem: Mail): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Mail, newItem: Mail): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private var onItemClickListener: ((Mail) -> Unit)? = null

    private val differ = AsyncListDiffer(this, diffCallback)

    private val colors = context.resources.getIntArray(R.array.colors)
    private val colorsLength = colors.size

    var mails: List<Mail>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MailItemViewHolder {
        return MailItemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_mail_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MailItemViewHolder, position: Int) {
        setContent(holder, position)
    }

    override fun getItemCount(): Int {
        return mails.size
    }

    fun setOnItemClickListener(onItemClick: (Mail) -> Unit) {
        this.onItemClickListener = onItemClick
    }

    @SuppressLint("SimpleDateFormat")
    private fun setContent(holder: MailItemViewHolder, position: Int) {
        val mail = mails[position]
        val binding = MailItemBinding.bind(holder.itemView)
        val sender =
            if (mail.flag.contains('s')) mail.addresses.first() else mail.addresses.last()
        val name =
            if (sender.name.isNotEmpty()) sender.name else sender.email.substringBefore('@')
        if (position == 0) {
            binding.apply {
                val dateFormat = SimpleDateFormat(Constants.DATE_FORMAT_FULL)
                val color = colors[mail.size % colorsLength]
                textViewSender.text = name.first().toString()
                imageViewSender.setColorFilter(color)
                textViewDate.text =
                    dateFormat.format(mail.time)
                textViewMailSubject.text = mail.subject
                textViewSenderEmail.text = name
                textViewSenderEmail.text = sender.email
                if (mail.flag.contains('a')) {
                    imageViewAttachment.isVisible = true
                }
//                if (mail.body.isEmpty()) {
//                    (activity as ActivityExt).showSnackbar("This mail has no content")
//                }
//                webView.loadDataWithBaseURL(
//                    Constants.BASE_URL,
//                    mail.parsedBody,
//                    "text/html",
//                    "utf-8",
//                    null
//                )
            }
        } else {

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
            binding.apply {
                textViewSender.text = name.first().toString()
                imageViewSender.setColorFilter(colors.random())
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
        }
        holder.itemView.setOnClickListener {
            onItemClickListener?.let { click ->
                click(mail)
            }
        }
    }
}