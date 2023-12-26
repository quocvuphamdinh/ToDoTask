package vu.pham.todotaskapp.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import vu.pham.todotaskapp.models.Task
import vu.pham.todotaskapp.service.ToDoService
import vu.pham.todotaskapp.utils.ServiceActions
import java.time.ZoneId

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == ServiceActions.NOTIFY_DAILY.toString()) {
            Intent(context, ToDoService::class.java).also {
                it.action = intent.action
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context?.startForegroundService(it)
                } else {
                    context?.startService(it)
                }
            }
            Log.d("hivu", "Message: Daily")
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
        val alarmManager = context?.getSystemService(AlarmManager::class.java)
        alarmManager?.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + (1 * 60 * 1000),
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
        Log.d("hivu", "Message: ${alarmItem?.message}")
    }
}