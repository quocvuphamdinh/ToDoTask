package vu.pham.todotaskapp.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import vu.pham.todotaskapp.models.Task
import vu.pham.todotaskapp.utils.ServiceActions
import java.time.ZoneId

class AndroidAlarmScheduler(
    private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)
    override fun schedule(item: AlarmItem, task: Task) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            val bundle = Bundle()
            bundle.putParcelable("EXTRA_ALARM", item)
            bundle.putParcelable("task", task)
            putExtras(bundle)
        }
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            item.time.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
            PendingIntent.getBroadcast(
                context,
                item.id.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
        val intentDaily = Intent(context, AlarmReceiver::class.java).apply {
            action = ServiceActions.NOTIFY_DAILY.toString()
        }
        val time7am = 1703548800L
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            time7am,
            //AlarmManager.INTERVAL_DAY,
            1 * 60 * 1000,
            PendingIntent.getBroadcast(
                context,
                -22,
                intentDaily,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    override fun cancel(item: AlarmItem) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                item.id.toInt(),
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}