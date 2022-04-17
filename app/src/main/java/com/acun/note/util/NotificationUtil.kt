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
import com.acun.note.util.Constants.NOTIFICATION_CHANNEL
import com.acun.note.util.Constants.NOTIFICATION_ID

fun NotificationManager.sendNotification(message: String, context: Context) {

    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, intent, PendingIntent.FLAG_IMMUTABLE)

    val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

    val notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
        .setContentTitle(context.getString(R.string.app_name))
        .setContentText(message)
        .setSmallIcon(R.mipmap.ic_launcher)
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

    notify(NOTIFICATION_ID, notificationBuilder.build())
}

fun NotificationManager.cancelNotification() {
    cancelAll()
}