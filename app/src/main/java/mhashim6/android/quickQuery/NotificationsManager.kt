package mhashim6.android.quickQuery

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

private const val CHANNEL_ID = "QUICK_QUERY_CHANNEL"

@RequiresApi(Build.VERSION_CODES.O)
fun createNotificationsChannel(context: Context) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val title = context.getString(R.string.app_name)
    val description = context.getString(R.string.running)

    val channel = NotificationChannel(CHANNEL_ID, title, NotificationManager.IMPORTANCE_DEFAULT)
    channel.description = description
    notificationManager.createNotificationChannel(channel)
}

@RequiresApi(Build.VERSION_CODES.O)
fun buildForegroundNotification(context: Context): Notification {
    val mainActivityStarter = Intent(context, MainActivity::class.java)
    val notificationAction = PendingIntent.getActivity(context, 0, mainActivityStarter, FLAG_IMMUTABLE)

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
    builder.setOngoing(true)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentIntent(notificationAction)
            .setSmallIcon(R.drawable.notification_search)
            .setPriority(Notification.PRIORITY_MIN)
            .setContentText(context.getString(R.string.running))
    return builder.build()
}