package com.acun.note.ui.note

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.db.SimpleSQLiteQuery
import com.acun.note.model.FolderModel
import com.acun.note.model.NoteModel
import com.acun.note.repository.Repository
import com.acun.note.util.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private val _noteList = MutableLiveData<ViewState<List<NoteModel>>>()
    val noteList: LiveData<ViewState<List<NoteModel>>> = _noteList

    private val _folderList = MutableLiveData<ViewState<List<FolderModel>>>()
    val folderList: LiveData<ViewState<List<FolderModel>>> = _folderList

    private val  _insertState = MutableLiveData<Boolean>()
    val insertState: LiveData<Boolean> = _insertState

    private val _note =  MutableLiveData<ViewState<NoteModel>>()
    val note: LiveData<ViewState<NoteModel>> = _note

    private val _noteMap = MutableLiveData<Map<Int, NoteModel>>()
    val noteMap: LiveData<Map<Int, NoteModel>> = _noteMap

    fun selectNote(currentMap: Map<Int, NoteModel>) {
        _noteMap.postValue(currentMap)
    }

    fun getNoteByFolder(id: Int) {
        viewModelScope.launch {
            repository.getNoteByFolder(id).collect { result ->
                try {
                    if (result.isNullOrEmpty()) {
                        _noteList.postValue(ViewState.Empty())
                    } else {
                        _noteList.postValue(ViewState.Success(data = result))
                    }
                } catch (e: IOException) {
                    _noteList.postValue(ViewState.Error(message = e.message))
                } catch (e: Exception) {
                    _noteList.postValue(ViewState.Error(message = e.message))
                }
            }
        }
    }

    fun getAllNote(query: SimpleSQLiteQuery) {
        viewModelScope.launch {
            repository.getAllNote(query).collect { result ->
                try {
                    if (result.isNullOrEmpty()) {
                        _noteList.postValue(ViewState.Empty())
                    } else {
                        _noteList.postValue(ViewState.Success(data = result))
                    }
                } catch (e: IOException) {
                    _noteList.postValue(ViewState.Error(message = e.message))
                } catch (e: Exception) {
                    _noteList.postValue(ViewState.Error(message = e.message))
                }
            }
        }
    }

    fun insertNote(note: NoteModel) {
        viewModelScope.launch {
            try {
                repository.insertNote(note)
                _insertState.postValue(true)
            } catch (e: Exception) {
                _insertState.postValue(false)
            }
        }
    }

    fun deleteNote(noteList: List<NoteModel>) {
        noteList.forEach {
            viewModelScope.launch {
                repository.deleteNote(it)
            }
        }
    }

    fun updateNote(note: NoteModel) {
        viewModelScope.launch {
            try {
                repository.updateNote(note)
                _insertState.postValue(true)
            } catch (e: Exception) {
                _insertState.postValue(false)
            }
        }
    }

    fun getFolder() {
        viewModelScope.launch {
            repository.getFolder().collect {
                if (it.isEmpty()) {
                    _folderList.postValue(ViewState.Empty())
                } else {
                    _folderList.postValue(ViewState.Success(data = it))
                }
            }
        }
    }

    fun moveNote(noteList: List<NoteModel>) {
        noteList.forEach {
            viewModelScope.launch {
                repository.updateNote(it)
            }
        }
    }
}