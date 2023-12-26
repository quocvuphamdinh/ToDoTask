package vu.pham.todotaskapp.utils

import vu.pham.todotaskapp.models.Task
import java.util.Date

object Constants {
    const val HIGH_PRIORITY = 1
    const val MEDIUM_PRIORITY = 2
    const val LOW_PRIORITY = 3
    const val DATABASE_NAME = "ToDoTaskDatabase"
    const val ALARM_TITLE = "Don't forget to do your task"
    fun alarmContent(task: Task) = "Task name: ${task.name}\nDate: ${
        DateUtils.convertDateFormat(
            Date(task.taskDate), "dd/MM/yyyy"
        )
    }\nTime: ${
        DateUtils.convertDateFormat(
            Date(task.startTime),
            "hh:mm a"
        )
    } - ${DateUtils.convertDateFormat(Date(task.endTime), "hh:mm a")}"

    fun alarmContentWithList(tasks: List<Task>): String {
        var result = ""
        for (item in tasks) {
            result += "${item.name}\n"
        }
        result += "..."
        return result
    }
}