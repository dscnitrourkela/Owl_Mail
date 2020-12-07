package github.sachin2dehury.nitrmail.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import github.sachin2dehury.nitrmail.R
import github.sachin2dehury.nitrmail.api.data.Mail

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
        holder.itemView.apply {

        }
    }

    override fun getItemCount(): Int {
        return mails.size
    }
}