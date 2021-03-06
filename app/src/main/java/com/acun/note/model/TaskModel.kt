package com.acun.note.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.acun.note.util.Constants.DESCRIPTION
import com.acun.note.util.Constants.ID
import com.acun.note.util.Constants.IMPORTANCE
import com.acun.note.util.Constants.IS_COMPLETE
import com.acun.note.util.Constants.TASK_TABLE_NAME
import com.acun.note.util.Constants.TITLE
import com.acun.note.util.Constants.URGENCY
import org.jetbrains.annotations.NotNull

@Entity(tableName = TASK_TABLE_NAME)
data class TaskModel(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    val id: Int?,

    @NotNull
    @ColumnInfo(name = TITLE)
    val title: String,

    @NotNull
    @ColumnInfo(name = DESCRIPTION)
    val description: String,

    @NotNull
    @ColumnInfo(name = IMPORTANCE)
    val importance: Float,

    @NotNull
    @ColumnInfo(name = URGENCY)
    val urgency: Float,

    @NotNull
    @ColumnInfo(name = IS_COMPLETE)
    val isCompleted: Boolean
)
