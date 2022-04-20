package com.acun.note.util

import androidx.sqlite.db.SimpleSQLiteQuery
import com.acun.note.util.Constants.CREATE_AT
import com.acun.note.util.Constants.ID
import com.acun.note.util.Constants.NOTE_TABLE_NAME
import com.acun.note.util.Constants.TITLE

fun getSortedQuery(sortBy: SortBy, type: SortType): SimpleSQLiteQuery {
    val query = StringBuilder("SELECT * FROM $NOTE_TABLE_NAME ")
    when (sortBy) {
        SortBy.CREATED -> query.append("ORDER BY $ID $type")
        SortBy.MODIFIED -> query.append("ORDER BY $CREATE_AT $type")
        SortBy.TITLE -> query.append("ORDER BY $TITLE $type")
    }
    return SimpleSQLiteQuery(query.toString())
}