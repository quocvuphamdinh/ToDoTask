package vu.pham.todotaskapp.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.Service
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vu.pham.todotaskapp.R
import vu.pham.todotaskapp.ToDoApplication
import vu.pham.todotaskapp.alarm.AlarmItem
import vu.pham.todotaskapp.models.Task
import vu.pham.todotaskapp.ui.activity.MainActivity
import vu.pham.todotaskapp.utils.Constants
import vu.pham.todotaskapp.utils.ServiceActions


class ToDoService : Service() {

    private var isServiceRunning = false
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ServiceActions.START.toString() -> {
                val bundle = intent.extras
                val alarmItem = bundle?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        it.getParcelable("EXTRA_ALARM", AlarmItem::class.java)
                    } else {
                        it.getParcelable("EXTRA_ALARM") as AlarmItem?
                    }
                }
                val task = bundle?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        it.getParcelable("task", Task::class.java)
                    } else {
                        it.getParcelable("task") as Task?
                    }
                }
                start(alarmItem, task)
            }

            ServiceActions.STOP.toString() -> {
                isServiceRunning = false
                stopSelf()
            }

            ServiceActions.NOTIFY_DAILY.toString() -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val taskRepository = (application as ToDoApplication).taskRepository
                    val listTaskDaily = taskRepository.getDailyTasksCompletedOrNotCompleted(0, 3)
                    listTaskDaily.collect {
                        if(it.isEmpty()){
                            return@collect
                        }
                        val notificationManager =
                            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                        val notification =
                            NotificationCompat.Builder(this@ToDoService, "todotask_channel")
                                .setOngoing(false)
                                .setAutoCancel(true)
                                .setSmallIcon(R.drawable.ic_to_do_list)
                                .setContentTitle("Your daily tasks")
                                .setStyle(
                                    NotificationCompat.BigTextStyle()
                                        .bigText(Constants.alarmContentWithList(it))
                                )
                                .setContentIntent(getActivityPendingIntentDailyTasks())
                                .build()

                        if (!isServiceRunning) {
                            startForeground(-22, notification)
                        } else {
                            notificationManager.notify(-22, notification)
                        }
                    }
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        isServiceRunning = true
        val notification = NotificationCompat.Builder(this, "todotask_channel")
            .setOngoing(false)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_to_do_list)
            .setContentTitle("Vũ To Do Task")
            .setContentText("This app will keep remind you to do your task")
            .build()
        startForeground(-1, notification)
    }

    private fun start(alarmItem: AlarmItem?, task: Task?) {
        if (alarmItem == null) {
            return
        }
        if (task == null) {
            return
        }
        isServiceRunning = true
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(this, "todotask_channel")
            .setOngoing(false)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_to_do_list)
            .setContentTitle(alarmItem.title)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(alarmItem.message)
            )
            .setContentIntent(getActivityPendingIntentTask(task))
            .build()

        if (!isServiceRunning) {
            startForeground(alarmItem.id.toInt(), notification)
        } else {
            notificationManager.notify(alarmItem.id.toInt(), notification)
        }
    }

    private fun getActivityPendingIntentTask(task: Task): PendingIntent {
        return PendingIntent.getActivity(
            this@ToDoService,
            task.id!!.toInt(),
            Intent(
                this@ToDoService,
                MainActivity::class.java
            )
                .also {
                    val bundle = Bundle()
                    bundle.putParcelable("task", task)
                    it.putExtras(bundle)
                    it.action = ServiceActions.SHOW_TASK.toString()
                    it.flags = FLAG_ACTIVITY_CLEAR_TOP
                }, FLAG_UPDATE_CURRENT
        )
    }

    private fun getActivityPendingIntentDailyTasks(): PendingIntent {
        return PendingIntent.getActivity(
            this@ToDoService,
            -22,
            Intent(
                this@ToDoService,
                MainActivity::class.java
            )
                .also {
                    it.action = ServiceActions.NOTIFY_DAILY.toString()
                    it.flags = FLAG_ACTIVITY_CLEAR_TOP
                }, FLAG_UPDATE_CURRENT
        )
    }

    override fun onDestroy() {
        isServiceRunning = false
        super.onDestroy()
    }
}