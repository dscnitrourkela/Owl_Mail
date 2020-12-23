package github.sachin2dehury.nitrmail.adapters

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import github.sachin2dehury.nitrmail.R
import github.sachin2dehury.nitrmail.api.data.mails.Mail
import github.sachin2dehury.nitrmail.databinding.ListMailItemBinding
import github.sachin2dehury.nitrmail.others.Constants
import java.text.SimpleDateFormat

class MailBoxAdapter : RecyclerView.Adapter<MailBoxAdapter.MailBoxViewHolder>(), Filterable {

    class MailBoxViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val diffCallback = object : DiffUtil.ItemCallback<Mail>() {

        override fun areItemsTheSame(oldItem: Mail, newItem: Mail): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Mail, newItem: Mail): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private var onItemClickListener: ((Mail) -> Unit)? = null

    @SuppressLint("SimpleDateFormat")
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
            if (mail.isUnread == "u") {
                textViewSender.typeface = Typeface.DEFAULT_BOLD
                textViewMailSubject.typeface = Typeface.DEFAULT_BOLD
                textViewDate.typeface = Typeface.DEFAULT_BOLD
                textViewMailBody.typeface = Typeface.DEFAULT_BOLD
            }
        }
        holder.itemView.setOnClickListener {
            onItemClickListener?.let { click ->
                click(mail)
            }
        }
    }

    override fun getItemCount(): Int {
        return mails.size
    }

    fun setOnItemClickListener(onItemClick: (Mail) -> Unit) {
        this.onItemClickListener = onItemClick
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(value: CharSequence?): FilterResults {
                val search = value.toString()
                val filterResults = FilterResults()
                filterResults.values = if (search.isEmpty()) {
                    mails
                } else {
                    mails.filter {
                        it.senders.first().email.contains(search, true) || it.body.contains(
                            search,
                            true
                        ) ||
                                it.subject.contains(search, true)
                    }
                }
                return filterResults
            }

            override fun publishResults(value: CharSequence?, filterResults: FilterResults?) {
                mails = filterResults?.values as List<Mail>
            }
        }
    }
}