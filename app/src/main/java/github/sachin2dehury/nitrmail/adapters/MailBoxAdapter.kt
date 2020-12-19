package github.sachin2dehury.nitrmail.adapters

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
            textViewDate.text = mail.time.toString()
            textViewMailBody.text = mail.body
            textViewMailSubject.text = mail.subject
            textViewSender.text = mail.senders.last().email
        }
        holder.itemView.setOnClickListener {
            navController.navigate(R.id.action_mailBoxFragment_to_mail_item_Fragment)
        }
    }

    override fun getItemCount(): Int {
        return mails.size
    }
}