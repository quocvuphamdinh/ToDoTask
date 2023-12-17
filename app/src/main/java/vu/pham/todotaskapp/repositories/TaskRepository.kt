package vu.pham.todotaskapp.repositories

import kotlinx.coroutines.flow.Flow
import vu.pham.todotaskapp.models.Task

interface TaskRepository {
    suspend fun createTask(task: Task)

    fun getTodayTasks(size: Int): Flow<List<Task>>

    fun getTotalTodayTasksCompletedOrNotCompleted(isCompleted: Int): Flow<Int>

    fun getDailyTasksCompletedOrNotCompleted(isCompleted: Int): Flow<List<Task>>

    fun getTotalDailyTasksCompletedOrNotCompleted(isCompleted: Int): Flow<Int>
}