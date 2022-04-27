package com.acun.note.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.acun.note.databinding.FolderItemBinding
import com.acun.note.model.FolderModel

class FolderAdapter : ListAdapter<FolderModel, FolderAdapter.FolderViewHolder>(FolderDiffCallback) {

    private var onItemLongClickListener: OnItemClickListener? = null
    fun setOnItemLongClickListener(listener: OnItemClickListener) {
        onItemLongClickListener = listener
    }

    private var onItemClickListener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val binding = FolderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FolderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    override fun getItemCount(): Int = currentList.size

    inner class FolderViewHolder(private val binding: FolderItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(folder: FolderModel) {
            binding.nameTextView.text = folder.folder
            binding.root.setOnClickListener {
                onItemClickListener?.onItemClicked(adapterPosition, folder)
            }
            binding.root.setOnLongClickListener {
                onItemLongClickListener?.onItemClicked(adapterPosition, folder)
                return@setOnLongClickListener true
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(pos: Int, folder: FolderModel)
    }

    companion object FolderDiffCallback: DiffUtil.ItemCallback<FolderModel>() {
        override fun areItemsTheSame(oldItem: FolderModel, newItem: FolderModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FolderModel, newItem: FolderModel): Boolean {
            return oldItem == newItem
        }
    }
}
