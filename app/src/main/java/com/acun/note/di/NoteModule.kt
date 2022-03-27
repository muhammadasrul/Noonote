package com.acun.note.di

import android.app.Application
import androidx.room.Room
import com.acun.note.data.db.NoteDatabase
import com.acun.note.repository.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NoteModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): NoteDatabase =
        Room.databaseBuilder(app, NoteDatabase::class.java, "note_db").build()

    @Provides
    @Singleton
    fun provideRepository(db: NoteDatabase): Repository = Repository(db.taskDao())
}