package com.acun.note.ui.project

import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.acun.note.R
import com.acun.note.databinding.AddFolderLayoutBinding
import com.acun.note.databinding.FragmentFolderBinding
import com.acun.note.model.FolderModel
import com.acun.note.ui.adapter.FolderAdapter
import com.acun.note.util.ViewState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FolderFragment : Fragment() {

    private var _binding: FragmentFolderBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FolderViewModel by viewModels()

    private val folderMap = mutableMapOf<Int, FolderModel>()
    private val folderAdapter = FolderAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFolderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addFolderBinding = AddFolderLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        val alertDialog = MaterialAlertDialogBuilder(requireContext(), R.style.Theme_NoteApp_MaterialAlertDialog_Rounded)
            .setView(addFolderBinding.root)
            .create()

        addFolderBinding.cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

        binding.folderRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.folderRecyclerView.adapter = folderAdapter
        folderAdapter.setOnItemClickListener(object : FolderAdapter.OnItemClickListener {
            override fun onItemClicked(pos: Int, folder: FolderModel) {
                findNavController().navigate(FolderFragmentDirections.actionProjectFragmentToNoteFragment(folder = folder))
            }
        })
        folderAdapter.setOnItemLongClickListener(object : FolderAdapter.OnItemClickListener {
            override fun onItemClicked(pos: Int, folder: FolderModel) {
                folderMap[pos] = folder
                viewModel.selectFolder(folderMap)
            }
        })

        viewModel.folderList.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ViewState.Success -> {
                    binding.emptyState.root.visibility = View.GONE
                    binding.addFolderFab.visibility = View.VISIBLE
                    folderAdapter.submitList(state.data)
                }
                is ViewState.Empty -> {
                    binding.emptyState.root.visibility = View.VISIBLE
                    binding.addFolderFab.visibility = View.GONE
                    binding.emptyState.addTaskButton.text = "Add folder"
                    binding.emptyState.titleTextView.text = "No folder yet"
                    binding.emptyState.descTextView.text = "Be sure to add your first folder"
                }
            }
        }

        binding.addFolderFab.setOnClickListener {
            alertDialog.show()
            addFolderBinding.saveButton.setOnClickListener {
                viewModel.insertFolder(addFolderBinding.nameEditText.text.toString())
                addFolderBinding.nameEditText.setText("")
                alertDialog.dismiss()
            }
        }

        binding.emptyState.addTaskButton.setOnClickListener {
            alertDialog.show()

            addFolderBinding.saveButton.setOnClickListener {
                viewModel.insertFolder(addFolderBinding.nameEditText.text.toString())
                addFolderBinding.nameEditText.setText("")
                alertDialog.dismiss()
            }
        }

        viewModel.folderMap.observe(viewLifecycleOwner) { map ->
            folderMap.putAll(map)
            folderMap.keys.forEach { pos ->
                binding.folderRecyclerView.findViewHolderForAdapterPosition(pos)?.itemView?.findViewById<CheckBox>(R.id.checkbox)?.isChecked = true
            }
            if (folderMap.isEmpty()) {
                setHasOptionsMenu(false)
                for (i in 0 until folderAdapter.itemCount) {
                    binding.folderRecyclerView.findViewHolderForAdapterPosition(i)?.itemView?.findViewById<CheckBox>(R.id.checkbox)?.visibility = View.GONE
                }
                folderAdapter.setOnItemClickListener(object : FolderAdapter.OnItemClickListener {
                    override fun onItemClicked(pos: Int, folder: FolderModel) {
                        findNavController().navigate(FolderFragmentDirections.actionProjectFragmentToNoteFragment(folder = folder))
                    }
                })
            } else {
                setHasOptionsMenu(true)
                for (i in 0 until folderAdapter.itemCount) {
                    binding.folderRecyclerView.findViewHolderForAdapterPosition(i)?.itemView?.findViewById<CheckBox>(R.id.checkbox)?.visibility = View.VISIBLE
                }
                folderAdapter.setOnItemClickListener(object : FolderAdapter.OnItemClickListener {
                    override fun onItemClicked(pos: Int, folder: FolderModel) {
                        if (folderMap.containsKey(pos)) {
                            folderMap.remove(pos)
                            binding.folderRecyclerView.findViewHolderForAdapterPosition(pos)?.itemView?.findViewById<CheckBox>(R.id.checkbox)?.isChecked = false
                        } else {
                            folderMap[pos] = folder
                        }
                        viewModel.selectFolder(folderMap)
                    }
                })
            }
        }

    }

    private fun deleteFolder(folderList: List<FolderModel>) {
        MaterialAlertDialogBuilder(requireContext(), R.style.Theme_NoteApp_MaterialAlertDialog_Rounded)
            .setTitle("Delete")
            .setMessage("Are you sure?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteFolder(folderList)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.delete_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_menu -> {
                val folderList: List<FolderModel> = folderMap.values.toList()
                deleteFolder(folderList)
                folderMap.clear()
                viewModel.selectFolder(folderMap)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        super.onPause()
        folderMap.clear()
        viewModel.selectFolder(folderMap)
    }
}