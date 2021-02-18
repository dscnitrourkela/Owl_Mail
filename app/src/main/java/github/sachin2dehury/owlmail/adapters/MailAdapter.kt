package github.sachin2dehury.owlmail.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import github.sachin2dehury.owlmail.api.data.Mail

open class MailAdapter(private val layout: Int) :
    RecyclerView.Adapter<MailAdapter.MailViewHolder>() {

    class MailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val diffCallback = object : DiffUtil.ItemCallback<Mail>() {

        override fun areItemsTheSame(oldItem: Mail, newItem: Mail): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Mail, newItem: Mail): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    @Suppress("LeakingThis")
    private val differ = AsyncListDiffer(this, diffCallback)

    var mails: List<Mail>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MailViewHolder(LayoutInflater.from(parent.context).inflate(layout, parent, false))

    override fun onBindViewHolder(holder: MailViewHolder, position: Int) {}

    override fun getItemCount() = mails.size

}