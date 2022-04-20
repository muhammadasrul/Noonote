package com.acun.note.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.acun.note.databinding.FolderListItemBinding
import com.acun.note.model.FolderModel

class FolderListAdapter(
    private val folderList: List<FolderModel>,
    val onItemClicked: (folderMode: FolderModel) ->Unit
) : RecyclerView.Adapter<FolderListAdapter.FolderListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderListViewHolder {
        val binding = FolderListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FolderListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FolderListViewHolder, position: Int) {
        holder.bind(folderList[position])
    }

    override fun getItemCount(): Int = folderList.size

    inner class FolderListViewHolder(private val binding: FolderListItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(folder: FolderModel) {
            binding.nameTextView.text = folder.folder
            binding.root.setOnClickListener {
                onItemClicked(folder)
            }
        }
    }
}
