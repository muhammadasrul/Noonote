package com.acun.note.ui.project

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acun.note.model.FolderModel
import com.acun.note.model.NoteModel
import com.acun.note.repository.Repository
import com.acun.note.util.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private val _folderList = MutableLiveData<ViewState<List<FolderModel>>>()
    val folderList: LiveData<ViewState<List<FolderModel>>> = _folderList

    private val _folderMap = MutableLiveData<Map<Int, FolderModel>>()
    val folderMap: LiveData<Map<Int, FolderModel>> = _folderMap

    fun selectFolder(currentMap: Map<Int, FolderModel>) {
        _folderMap.postValue(currentMap)
    }

    init {
        getFolder()
    }

    private fun getFolder() {
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

    fun insertFolder(name: String) {
        viewModelScope.launch {
            repository.insertFolder(name)
        }
    }

    fun deleteFolder(folderList: List<FolderModel>) {
        folderList.forEach { folder ->
            viewModelScope.launch {
                repository.deleteFolder(folder)
            }
        }
    }
}