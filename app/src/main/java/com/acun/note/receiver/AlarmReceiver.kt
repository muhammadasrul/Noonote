package com.acun.note.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import androidx.core.content.ContextCompat
import com.acun.note.util.Constants.timerDataStoreName
import com.acun.note.util.Constants.triggerTime
import com.acun.note.util.sendNotification

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {

        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        val pref = context.getSharedPreferences(timerDataStoreName, Context.MODE_PRIVATE)

        notificationManager.sendNotification("Notif nih", context)
    }
}