package com.acun.note.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import com.acun.note.ui.MainActivity
import com.acun.note.R
import com.acun.note.util.Constants.notificationChannel
import com.acun.note.util.Constants.notificationId

fun NotificationManager.sendNotification(message: String, context: Context) {

    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_MUTABLE)

    val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

    val notificationBuilder = NotificationCompat.Builder(context, notificationChannel)
        .setContentTitle("Note App")
        .setContentText(message)
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setSound(notificationSound)
        .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
        .setAutoCancel(true)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        notificationBuilder.setSmallIcon(
            IconCompat.createWithResource(context, R.drawable.ic_launcher_foreground)
        )
    }

    notify(notificationId, notificationBuilder.build())
}

fun NotificationManager.cancelNotification() {
    cancelAll()
}