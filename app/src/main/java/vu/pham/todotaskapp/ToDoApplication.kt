package vu.pham.todotaskapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import vu.pham.todotaskapp.alarm.AndroidAlarmScheduler
import vu.pham.todotaskapp.database.ToDoDatabase
import vu.pham.todotaskapp.repositories.TaskRepository
import vu.pham.todotaskapp.repositories.TaskRepositoryImpl

class ToDoApplication : Application() {
    val database by lazy { ToDoDatabase.getInstance(this) }
    val taskRepository: TaskRepository by lazy { TaskRepositoryImpl(database.getToDoDAO()) }
    val scheduler by lazy { AndroidAlarmScheduler(this) }

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "todotask_channel",
                "To Do Task By VuPham's Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}