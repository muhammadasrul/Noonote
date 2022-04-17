package com.acun.note.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.acun.note.util.Constants.CATEGORY_ID
import com.acun.note.util.Constants.CREATE_AT
import com.acun.note.util.Constants.DESCRIPTION
import com.acun.note.util.Constants.ID
import com.acun.note.util.Constants.NOTE_TABLE_NAME
import com.acun.note.util.Constants.TITLE
import kotlinx.parcelize.Parcelize
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

@Parcelize
@Entity(tableName = NOTE_TABLE_NAME)
data class NoteModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    val id: Int?,

    @NotNull
    @ColumnInfo(name = TITLE)
    val title: String,

    @NotNull
    @ColumnInfo(name = DESCRIPTION)
    val description: String,

    @Nullable
    @ColumnInfo(name = CATEGORY_ID)
    val category_id: Int?,

    @NotNull
    @ColumnInfo(name = CREATE_AT)
    val create_at: Long
): Parcelable
