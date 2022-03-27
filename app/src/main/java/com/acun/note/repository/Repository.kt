package com.acun.note.repository

import com.acun.note.data.db.NoteDao
import com.acun.note.model.TaskModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class Repository @Inject constructor(private val noteDao: NoteDao) {

    suspend fun insert(task: TaskModel) {
        noteDao.insert(task)
    }

    suspend fun update(task: TaskModel) {
        noteDao.update(task)
    }

    suspend fun delete(task: TaskModel) {
        noteDao.delete(task)
    }

    fun getAllTask(): Flow<List<TaskModel>> {
        return noteDao.getAllTask()
    }
}