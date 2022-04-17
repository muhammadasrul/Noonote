package com.acun.note.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.acun.note.util.Constants.IS_TIMER_ON
import com.acun.note.util.Constants.TIME_PREFERENCE_NAME
import com.acun.note.util.sendNotification

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {

        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        val pref = context.getSharedPreferences(TIME_PREFERENCE_NAME, Context.MODE_PRIVATE)
        pref.edit().putBoolean(IS_TIMER_ON, false).apply()

        notificationManager.sendNotification("Time's up", context)
    }
}