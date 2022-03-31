package com.acun.note.data.db

import androidx.room.*
import com.acun.note.model.TaskModel
import com.acun.note.util.Constants.NOTE_TABLE_NAME
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskModel)

    @Delete
    suspend fun delete(task: TaskModel)

    @Query("SELECT * FROM $NOTE_TABLE_NAME")
    fun getAllTask(): Flow<List<TaskModel>>

    @Update
    suspend fun update(task: TaskModel)
}