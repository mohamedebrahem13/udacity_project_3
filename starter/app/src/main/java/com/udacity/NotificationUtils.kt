package com.udacity

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

private val NOTIFICATION_ID = 1
@SuppressLint("UnspecifiedImmutableFlag")
fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context, fileName: String, status:String) {
    val detailIntent = Intent(applicationContext, DetailActivity::class.java)
        .putExtra("filename", fileName)
        .putExtra("status", status)

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        detailIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.download_channel_id)

    )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .addAction(R.drawable.ic_assistant_black_24dp,applicationContext.getString(R.string.notification_action),contentPendingIntent)
        .setAutoCancel(true)
    notify(NOTIFICATION_ID,builder.build())
}
// cancel notification extinction fun
fun NotificationManager.cancelNotification(){
    cancel(NOTIFICATION_ID)
}
