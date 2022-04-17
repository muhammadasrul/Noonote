package com.acun.note.ui.note

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.CheckBox
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.acun.note.R
import com.acun.note.databinding.FragmentNoteBinding
import com.acun.note.model.NoteModel
import com.acun.note.ui.MainActivity
import com.acun.note.ui.adapter.NoteAdapter
import com.acun.note.util.ViewState
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

        navArgs.folder?.id?.let {
            viewModel.getNoteByFolder(it)
        } ?: viewModel.getAllNote()

        (requireActivity() as MainActivity).run {
            findViewById<CardView>(R.id.bottomNavConntainer).visibility = if (navArgs.folder != null) View.GONE else View.VISIBLE
            supportActionBar?.title = navArgs.folder?.folder ?: "Notes"
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
            noteMap.putAll(map)
            noteMap.keys.forEach { pos ->
                binding.noteRecyclerView.findViewHolderForAdapterPosition(pos)?.itemView?.findViewById<CheckBox>(R.id.checkbox)?.isChecked = true
            }
            if (noteMap.isEmpty()) {
                setHasOptionsMenu(false)
                for (i in 0 until noteAdapter.itemCount) {
                    binding.noteRecyclerView.findViewHolderForAdapterPosition(i)?.itemView?.findViewById<CheckBox>(R.id.checkbox)?.visibility = View.GONE
                }
                noteAdapter.setOnItemClickListener(object : NoteAdapter.OnItemClickListener {
                    override fun onItemClicked(pos: Int, note: NoteModel) {
                        findNavController().navigate(NoteFragmentDirections.actionNoteFragmentToAddNoteFragment(note = note, folder = navArgs.folder))
                    }
                })
            } else {
                setHasOptionsMenu(true)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.delete_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_menu -> {
                val noteList: List<NoteModel> = noteMap.values.toList()
                viewModel.deleteNote(noteList)
                noteMap.clear()
                viewModel.selectNote(noteMap)
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
}