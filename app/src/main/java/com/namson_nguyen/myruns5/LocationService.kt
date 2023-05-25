package com.namson_nguyen.myruns5

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import androidx.core.app.NotificationCompat
import java.util.*

class LocationService : Service() {
    private lateinit var notificationManager: NotificationManager
    val NOTIFICATION_ID = 777
    private val CHANNEL_ID = "notification channel"
    private lateinit var myBinder: Binder
    private var msgHandler : Handler? = null
    private var counter = 0
    private lateinit var myTask: TimerTask
    private lateinit var timer: Timer
    companion object{
        val INT_KEY = "int_key"
        val INT_MSG = 1
    }
    private fun showNotification() { val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
            this,
            CHANNEL_ID
        ) //XD: see book p1019 why we do not use Notification.Builder
        notificationBuilder.setSmallIcon(R.drawable.ic_launcher_foreground)
        notificationBuilder.setContentTitle("Service has started")
        notificationBuilder.setContentText("Tap me to go back")
        notificationBuilder.setContentIntent(pendingIntent)
        val notification = notificationBuilder.build()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            "channel name",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(notificationChannel)
        notificationManager.notify(NOTIFICATION_ID, notification) }
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}