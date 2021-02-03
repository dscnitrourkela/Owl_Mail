package github.sachin2dehury.owlmail.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import github.sachin2dehury.owlmail.api.data.Mail

open class MailAdapter(private val layout: Int) :
    RecyclerView.Adapter<MailAdapter.MailViewHolder>(), Filterable {

    class MailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val diffCallback = object : DiffUtil.ItemCallback<Mail>() {

        override fun areItemsTheSame(oldItem: Mail, newItem: Mail): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Mail, newItem: Mail): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var list: List<Mail> = emptyList()

    var mails: List<Mail>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MailViewHolder(LayoutInflater.from(parent.context).inflate(layout, parent, false))

    override fun onBindViewHolder(holder: MailViewHolder, position: Int) {}

    override fun getItemCount() = mails.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(value: CharSequence?): FilterResults {
                val search = value.toString()
                val filterResults = FilterResults()
                filterResults.values = if (search.isEmpty()) {
                    list
                } else {
                    list.filter { mail ->
                        val sender =
                            if (mail.flag.contains('s')) mail.addresses.first().email else mail.addresses.last().email
                        sender.contains(search, true)
                                || mail.body.contains(search, true)
                                || mail.subject.contains(search, true)
                                || mail.parsedBody.contains(search, true)
                    }
                }
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(value: CharSequence?, filterResults: FilterResults?) {
                mails = filterResults?.values as List<Mail>
            }
        }
    }
}