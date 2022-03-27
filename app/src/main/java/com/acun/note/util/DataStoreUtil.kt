package com.acun.note.util

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.acun.note.util.Constants.timerDataStoreName
import com.acun.note.util.Constants.triggerTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.timerPreference by preferencesDataStore(name = timerDataStoreName)

class DataStoreUtil(private val context: Context) {

//    val loadTime = context.timerPreference.data.map { pref ->
//        pref[triggerTime] ?: 0
//    }
//
//    suspend fun saveTime(time: Long) {
//        context.timerPreference.edit { pref ->
//            pref[triggerTime] = time
//        }
//    }
}