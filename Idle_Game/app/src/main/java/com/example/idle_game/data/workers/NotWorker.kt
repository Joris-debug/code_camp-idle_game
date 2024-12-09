package com.example.idle_game.data.workers

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.idle_game.R

class NotWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        sendNotification()
        return Result.success()
    }

    @SuppressLint("NotificationPermission", "ObsoleteSdkInt")
    private fun sendNotification() {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannelId = "default_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationChannelId,
                "Default Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, notificationChannelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Not your Keys not your coin!")
            .setContentText("SChnell übertrage deine Bitcoins auf deine Cold-Wallet bevor sie verloren gehen!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(1, notification)
    }

}