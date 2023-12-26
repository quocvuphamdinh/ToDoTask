package vu.pham.todotaskapp.alarm

import vu.pham.todotaskapp.models.Task

interface AlarmScheduler {
    fun schedule(item: AlarmItem, task: Task)
    fun cancel(item: AlarmItem)
}