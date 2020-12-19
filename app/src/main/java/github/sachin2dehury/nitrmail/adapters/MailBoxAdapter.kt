package github.sachin2dehury.nitrmail.adapters

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import github.sachin2dehury.nitrmail.R
import github.sachin2dehury.nitrmail.api.data.Mail
import github.sachin2dehury.nitrmail.databinding.ListMailItemBinding
import github.sachin2dehury.nitrmail.others.Constants
import java.text.SimpleDateFormat

class MailBoxAdapter : RecyclerView.Adapter<MailBoxAdapter.MailBoxViewHolder>() {

    class MailBoxViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val diffCallback = object : DiffUtil.ItemCallback<Mail>() {

        override fun areItemsTheSame(oldItem: Mail, newItem: Mail): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Mail, newItem: Mail): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    lateinit var navController: NavController

    private val dateFormat = SimpleDateFormat(Constants.DATE_FORMAT_YEAR)

    private val differ = AsyncListDiffer(this, diffCallback)

    var mails: List<Mail>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MailBoxViewHolder {
        return MailBoxViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_mail_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MailBoxViewHolder, position: Int) {
        val mail = mails[position]
        val binding = ListMailItemBinding.bind(holder.itemView)
        binding.apply {
            textViewDate.text = dateFormat.format(mail.time)
            textViewMailBody.text = mail.body
            textViewMailSubject.text = mail.subject
            textViewSender.text = mail.senders.last().email
            if (mail.isUnread == "t") {
                textViewSender.typeface = Typeface.DEFAULT_BOLD
                textViewMailSubject.typeface = Typeface.DEFAULT_BOLD
                textViewDate.typeface = Typeface.DEFAULT_BOLD
                textViewMailBody.typeface = Typeface.DEFAULT_BOLD
            }
        }
        holder.itemView.setOnClickListener {
            navController.navigate(R.id.action_mailBoxFragment_to_mail_item_Fragment)
        }
    }

    override fun getItemCount(): Int {
        return mails.size
    }
}