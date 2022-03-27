package com.acun.note.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.acun.note.model.TaskModel

@Database(entities = [TaskModel::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun taskDao(): NoteDao
}