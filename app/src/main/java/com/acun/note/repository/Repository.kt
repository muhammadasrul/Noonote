package com.acun.note.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.acun.note.data.db.NoteDao
import com.acun.note.model.FolderModel
import com.acun.note.model.NoteModel
import com.acun.note.model.TaskModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class Repository @Inject constructor(private val noteDao: NoteDao) {

    suspend fun insertTask(task: TaskModel) {
        noteDao.insertTask(task)
    }

    suspend fun updateTask(task: TaskModel) {
        noteDao.updateTask(task)
    }

    suspend fun deleteTask(task: TaskModel) {
        noteDao.deleteTask(task)
    }

    fun getAllTask(): Flow<List<TaskModel>> {
        return noteDao.getAllTask()
    }

    suspend fun insertNote(note: NoteModel) {
        noteDao.insertNote(note)
    }

    suspend fun updateNote(note: NoteModel) {
        noteDao.updateNote(note)
    }

    suspend fun deleteNote(note: NoteModel) {
        noteDao.deleteNote(note)
    }

    fun getAllNote(query: SimpleSQLiteQuery): Flow<List<NoteModel>> {
        return noteDao.getAllNote(query)
    }

    fun getFolder(): Flow<List<FolderModel>> {
        return noteDao.getFolder()
    }

    suspend fun insertFolder(name: String) {
        return noteDao.insertFolder(FolderModel(null, name))
    }

    suspend fun deleteFolder(folder: FolderModel) {
        return noteDao.deleteFolder(folder)
    }

    fun getNoteByFolder(folder_id: Int): Flow<List<NoteModel>> {
        return noteDao.getNoteByFolder(folder_id)
    }
}