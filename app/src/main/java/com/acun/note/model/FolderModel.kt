package com.acun.note.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.acun.note.util.Constants.FOLDER_TABLE_NAME
import com.acun.note.util.Constants.ID
import com.acun.note.util.Constants.NAME
import kotlinx.parcelize.Parcelize
import org.jetbrains.annotations.NotNull

@Entity(tableName = FOLDER_TABLE_NAME)
@Parcelize
data class FolderModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    val id: Int?,

    @NotNull
    @ColumnInfo(name = NAME)
    val folder: String
): Parcelable
