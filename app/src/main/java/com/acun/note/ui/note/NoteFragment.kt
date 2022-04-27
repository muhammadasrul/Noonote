package com.acun.note.ui.note

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.acun.note.R
import com.acun.note.databinding.BottomSheetSortTypeBinding
import com.acun.note.databinding.FragmentNoteBinding
import com.acun.note.databinding.MoveNoteDialogBinding
import com.acun.note.model.NoteModel
import com.acun.note.ui.MainActivity
import com.acun.note.ui.adapter.FolderListAdapter
import com.acun.note.ui.adapter.NoteAdapter
import com.acun.note.util.SortBy
import com.acun.note.util.SortType
import com.acun.note.util.ViewState
import com.acun.note.util.getSortedQuery
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteFragment : Fragment() {

    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NoteViewModel by viewModels()
    private val noteAdapter = NoteAdapter()

    private val noteMap = mutableMapOf<Int, NoteModel>()
    private val navArgs: NoteFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        val mainActivity = requireActivity() as MainActivity

        navArgs.folder?.id?.let {
            viewModel.getNoteByFolder(it)
            mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        } ?: viewModel.getAllNote(getSortedQuery(SortBy.CREATED, SortType.ASC))

        mainActivity.run {
            findViewById<CardView>(R.id.bottomNavConntainer).visibility = if (navArgs.folder != null) View.GONE else View.VISIBLE
            supportActionBar?.title = navArgs.folder?.folder ?: "All Notes"
        }

        binding.addNoteFab.setOnClickListener {
            findNavController().navigate(NoteFragmentDirections.actionNoteFragmentToAddNoteFragment(folder = navArgs.folder))
        }
        binding.emptyState.addTaskButton.setOnClickListener {
            findNavController().navigate(NoteFragmentDirections.actionNoteFragmentToAddNoteFragment(folder = navArgs.folder))
        }

        binding.noteRecyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.noteRecyclerView.adapter = noteAdapter
        noteAdapter.setOnItemClickListener(object : NoteAdapter.OnItemClickListener {
            override fun onItemClicked(pos: Int, note: NoteModel) {
                findNavController().navigate(NoteFragmentDirections.actionNoteFragmentToAddNoteFragment(note = note, folder = navArgs.folder))
            }
        })
        noteAdapter.setOnItemLongClickListener(object : NoteAdapter.OnItemClickListener {
            override fun onItemClicked(pos: Int, note: NoteModel) {
                noteMap[pos] = note
                viewModel.selectNote(noteMap)
            }
        })

        viewModel.noteList.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ViewState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
                is ViewState.Success -> {
                    binding.emptyState.root.visibility = View.GONE
                    noteAdapter.submitList(state.data)
                }
                is ViewState.Empty -> {
                    binding.addNoteFab.visibility = View.GONE
                    binding.emptyState.root.visibility = View.VISIBLE
                    binding.emptyState.addTaskButton.text = "Add Note"
                    binding.emptyState.titleTextView.text = "No note yet"
                    binding.emptyState.descTextView.text = "Be sure to add your first note"
                }
            }
        }

        viewModel.noteMap.observe(viewLifecycleOwner) { map ->
            requireActivity().invalidateOptionsMenu()
            noteMap.putAll(map)
            noteMap.keys.forEach { pos ->
                binding.noteRecyclerView.findViewHolderForAdapterPosition(pos)?.itemView?.findViewById<CheckBox>(R.id.checkbox)?.isChecked = true
            }
            if (noteMap.isEmpty()) {
                for (i in 0 until noteAdapter.itemCount) {
                    binding.noteRecyclerView.findViewHolderForAdapterPosition(i)?.itemView?.findViewById<CheckBox>(R.id.checkbox)?.visibility = View.GONE
                }
                noteAdapter.setOnItemClickListener(object : NoteAdapter.OnItemClickListener {
                    override fun onItemClicked(pos: Int, note: NoteModel) {
                        findNavController().navigate(NoteFragmentDirections.actionNoteFragmentToAddNoteFragment(note = note, folder = navArgs.folder))
                    }
                })
            } else {
                for (i in 0 until noteAdapter.itemCount) {
                    binding.noteRecyclerView.findViewHolderForAdapterPosition(i)?.itemView?.findViewById<CheckBox>(R.id.checkbox)?.visibility = View.VISIBLE
                }
                noteAdapter.setOnItemClickListener(object : NoteAdapter.OnItemClickListener {
                    override fun onItemClicked(pos: Int, note: NoteModel) {
                        if (noteMap.containsKey(pos)) {
                            noteMap.remove(pos)
                            binding.noteRecyclerView.findViewHolderForAdapterPosition(pos)?.itemView?.findViewById<CheckBox>(R.id.checkbox)?.isChecked = false
                        } else {
                            noteMap[pos] = note
                        }
                        viewModel.selectNote(noteMap)
                    }
                })
            }
        }

    }

    private fun deleteNote(noteList: List<NoteModel>) {
        MaterialAlertDialogBuilder(requireContext(), R.style.Theme_NoteApp_MaterialAlertDialog_Rounded)
            .setTitle("Delete")
            .setMessage("Are you sure?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteNote(noteList)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun moveNote(noteList: List<NoteModel>) {
        val dialogBinding = MoveNoteDialogBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.Theme_NoteApp_MaterialAlertDialog_Rounded).apply {
            setView(dialogBinding.root)
            create()
        }.show()

        viewModel.getFolder()
        viewModel.folderList.observe(viewLifecycleOwner) { state ->
            if (state is ViewState.Empty) {
                dialogBinding.emptyFolderTextView.visibility = View.VISIBLE
            }
            if (state is ViewState.Success) {
                val listFolder = state.data?.toMutableList()
                navArgs.folder?.let { listFolder?.remove(it) }

                dialogBinding.folderRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                dialogBinding.folderRecyclerView.adapter = FolderListAdapter(listFolder ?: emptyList()) { folder ->
                    val newNoteList: MutableList<NoteModel>  = mutableListOf()
                    noteList.forEachIndexed { index, note ->
                        newNoteList.add(index, note.copy(category_id = folder.id))
                    }
                    viewModel.moveNote(newNoteList)
                    dialog.dismiss()
                    noteMap.clear()
                    viewModel.selectNote(noteMap)
                }
            }
        }
    }

    private fun showSortDialog() {
        val bottomSheet = BottomSheetSortTypeBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(bottomSheet.root)
        dialog.show()

        bottomSheet.listView.adapter = ArrayAdapter.createFromResource(requireContext(), R.array.sort_type, android.R.layout.simple_list_item_1)
        bottomSheet.listView.setOnItemClickListener { _, _, i, _ ->
            when (i) {
                0 -> viewModel.getAllNote(getSortedQuery(SortBy.TITLE, SortType.ASC))
                1 -> viewModel.getAllNote(getSortedQuery(SortBy.TITLE, SortType.DESC))
                2 -> viewModel.getAllNote(getSortedQuery(SortBy.MODIFIED, SortType.ASC))
                3 -> viewModel.getAllNote(getSortedQuery(SortBy.MODIFIED, SortType.DESC))
                4 -> viewModel.getAllNote(getSortedQuery(SortBy.CREATED, SortType.ASC))
                5 -> viewModel.getAllNote(getSortedQuery(SortBy.CREATED, SortType.DESC))
            }
            dialog.dismiss()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.note_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val noteList: List<NoteModel> = noteMap.values.toList()
        return when (item.itemId) {
            R.id.delete_menu -> {
                deleteNote(noteList)
                noteMap.clear()
                viewModel.selectNote(noteMap)
                return true
            }
            R.id.move_to_menu -> {
                moveNote(noteList)
                return true
            }
            R.id.sort_by_menu -> {
                showSortDialog()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        super.onPause()
        noteMap.clear()
        viewModel.selectNote(noteMap)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.clear()
        if (noteMap.isEmpty()) {
            requireActivity().menuInflater.inflate(R.menu.sort_menu, menu)
        } else {
            requireActivity().menuInflater.inflate(R.menu.note_menu, menu)
        }
    }
}