package github.sachin2dehury.owlmail.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.databinding.AttachmentsBinding

class AttachmentAdapter : RecyclerView.Adapter<AttachmentAdapter.AttachmentViewHolder>() {

    var onItemClickListener: ((String) -> Unit)? = null

    var attachmentsName: List<String> = emptyList()
    var attachmentsLink: List<String> = emptyList()

    class AttachmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        AttachmentViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.attachments, parent, false)
        )

    override fun onBindViewHolder(holder: AttachmentViewHolder, position: Int) {
        val binding = AttachmentsBinding.bind(holder.itemView)
        binding.apply {
            buttonAttachment.text = attachmentsName[position]
            buttonAttachment.setOnClickListener {
                onItemClickListener?.let { onClick -> onClick(attachmentsLink[position]) }
            }
        }
    }

    override fun getItemCount() = attachmentsName.size
}