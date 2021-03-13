package github.sachin2dehury.owlmail.adapters

import androidx.recyclerview.widget.DiffUtil

abstract class DiffCallBack<T> : DiffUtil.ItemCallback<T>() {
    override fun areContentsTheSame(oldItem: T, newItem: T) =
        oldItem.hashCode() == newItem.hashCode()
}