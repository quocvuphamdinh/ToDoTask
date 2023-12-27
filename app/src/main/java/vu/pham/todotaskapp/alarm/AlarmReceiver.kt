package vu.pham.todotaskapp.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import vu.pham.todotaskapp.models.Task
import vu.pham.todotaskapp.service.ToDoService
import vu.pham.todotaskapp.utils.ServiceActions

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val alarmManager = context?.getSystemService(AlarmManager::class.java)
        if (intent?.action == ServiceActions.NOTIFY_DAILY.toString()) {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 7)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.add(Calendar.DATE, 1)
            val time7am = calendar.timeInMillis
            alarmManager?.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time7am,
                PendingIntent.getBroadcast(
                    context,
                    -22,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
            Intent(context, ToDoService::class.java).also {
                it.action = intent.action
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context?.startForegroundService(it)
                } else {
                    context?.startService(it)
                }
            }
            return
        }
        val bundle = intent?.extras
        val alarmItem = bundle?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable("EXTRA_ALARM", AlarmItem::class.java)
            } else {
                it.getParcelable("EXTRA_ALARM") as AlarmItem?
            }
        }
        alarmManager?.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + AlarmManager.INTERVAL_HOUR,
            PendingIntent.getBroadcast(
                context,
                alarmItem?.id!!.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
        val task = bundle?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable("task", Task::class.java)
            } else {
                it.getParcelable("task") as Task?
            }
        }
        Intent(context, ToDoService::class.java).also {
            it.action = ServiceActions.START.toString()
            val b = Bundle()
            b.putParcelable("EXTRA_ALARM", alarmItem)
            b.putParcelable("task", task)
            it.putExtras(b)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context?.startForegroundService(it)
            } else {
                context?.startService(it)
            }
        }
    }
}