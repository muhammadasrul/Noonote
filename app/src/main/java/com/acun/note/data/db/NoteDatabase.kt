package com.acun.note.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.acun.note.model.FolderModel
import com.acun.note.model.NoteModel
import com.acun.note.model.TaskModel

@Database(entities = [TaskModel::class, NoteModel::class, FolderModel::class], version = 1, exportSchema = true)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun taskDao(): NoteDao
}