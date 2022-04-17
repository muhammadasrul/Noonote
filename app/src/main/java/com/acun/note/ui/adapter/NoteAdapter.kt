package com.acun.note.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.acun.note.databinding.NoteItemBinding
import com.acun.note.model.NoteModel
import java.text.SimpleDateFormat
import java.util.*

class NoteAdapter : ListAdapter<NoteModel, NoteAdapter.NoteViewHolder>(NoteDiffCallback) {

    private var onItemLongClickListener: OnItemClickListener? = null
    fun setOnItemLongClickListener(listener: OnItemClickListener) {
        onItemLongClickListener = listener
    }

    private var onItemClickListener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(currentList[position], position)
    }

    override fun getItemCount(): Int = currentList.size

    inner class NoteViewHolder(private val binding: NoteItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: NoteModel, position: Int) {
            with(binding) {
                titleTextView.text = note.title
                descTextView.text = note.description
                val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                dateTextView.text = sdf.format(note.create_at)
                root.setOnClickListener {
                    onItemClickListener?.onItemClicked(position, note)
                }
                root.setOnLongClickListener {
                    onItemLongClickListener?.onItemClicked(position, note)
                    return@setOnLongClickListener true
                }

            }
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(pos: Int, note: NoteModel)
    }

    companion object NoteDiffCallback: DiffUtil.ItemCallback<NoteModel>() {
        override fun areItemsTheSame(oldItem: NoteModel, newItem: NoteModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NoteModel, newItem: NoteModel): Boolean {
            return oldItem == newItem
        }
    }
}
