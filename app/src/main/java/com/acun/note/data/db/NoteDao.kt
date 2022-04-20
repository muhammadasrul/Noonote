package com.acun.note.data.db

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import com.acun.note.model.FolderModel
import com.acun.note.model.NoteModel
import com.acun.note.model.TaskModel
import com.acun.note.util.Constants.FOLDER_TABLE_NAME
import com.acun.note.util.Constants.NOTE_TABLE_NAME
import com.acun.note.util.Constants.TASK_TABLE_NAME
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskModel)

    @Delete
    suspend fun deleteTask(task: TaskModel)

    @Query("SELECT * FROM $TASK_TABLE_NAME")
    fun getAllTask(): Flow<List<TaskModel>>

    @Update
    suspend fun updateTask(task: TaskModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(task: NoteModel)

    @Delete
    suspend fun deleteNote(note: NoteModel)

    @RawQuery(observedEntities = [NoteModel::class])
    fun getAllNote(query: SimpleSQLiteQuery): Flow<List<NoteModel>>

    @Update
    suspend fun updateNote(task: NoteModel)

    @Query("SELECT * FROM $FOLDER_TABLE_NAME")
    fun getFolder(): Flow<List<FolderModel>>

    @Insert
    suspend fun insertFolder(folder: FolderModel)

    @Delete
    suspend fun deleteFolder(folder: FolderModel)

    @Query("SELECT * FROM $NOTE_TABLE_NAME WHERE category_id = :folderId")
    fun getNoteByFolder(folderId: Int): Flow<List<NoteModel>>
}