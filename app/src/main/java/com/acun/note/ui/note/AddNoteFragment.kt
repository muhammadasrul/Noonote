package com.acun.note.ui.note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.acun.note.databinding.FragmentAddNoteBinding
import com.acun.note.model.NoteModel
import com.acun.note.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddNoteFragment : Fragment() {

    private var _binding: FragmentAddNoteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NoteViewModel by viewModels()
    private val navArgs: AddNoteFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navArgs.note?.let { note ->
            binding.titleEditText.setText(note.title)
            binding.noteEditText.setText(note.description)
            (requireActivity() as MainActivity).supportActionBar?.title = "All Notes"
        }

        val title = binding.titleEditText
        val desc = binding.noteEditText

        if (navArgs.note != null) {
            binding.saveFab.setOnClickListener {
                if (title.text.isNullOrEmpty() || desc.text.isNullOrEmpty()) {
                    findNavController().navigateUp()
                }
                viewModel.updateNote(
                    NoteModel(
                        id = navArgs.note?.id,
                        title = title.text.toString(),
                        description = desc.text.toString(),
                        category_id = navArgs.note?.category_id,
                        create_at = System.currentTimeMillis()
                    )
                )
            }
        } else {
            binding.saveFab.setOnClickListener {
                if (title.text.isNullOrEmpty() || desc.text.isNullOrEmpty()) {
                    findNavController().navigateUp()
                }

                val folderId = if (navArgs.folder != null) {
                    navArgs.folder?.id
                } else {
                    navArgs.folder?.id
                }

                viewModel.insertNote(
                    NoteModel(
                        id = null,
                        title = title.text.toString(),
                        description = desc.text.toString(),
                        category_id = folderId,
                        create_at = System.currentTimeMillis()
                    )
                )
            }
        }

        viewModel.insertState.observe(viewLifecycleOwner) { state ->
            if (state) {
                findNavController().navigateUp()
            }
        }
    }
}