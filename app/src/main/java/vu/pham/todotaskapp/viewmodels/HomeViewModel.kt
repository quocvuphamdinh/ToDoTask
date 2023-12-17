package vu.pham.todotaskapp.viewmodels

import androidx.lifecycle.ViewModel
import vu.pham.todotaskapp.repositories.TaskRepository

class HomeViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {
    fun getTodayTasks(size: Int) = taskRepository.getTodayTasks(size)

    fun getTotalTodayTasksCompletedOrNotCompleted(isCompleted: Boolean) =
        taskRepository.getTotalTodayTasksCompletedOrNotCompleted(if (isCompleted) 1 else 0)

    fun getDailyTasksCompletedOrNotCompleted(isCompleted: Boolean) =
        taskRepository.getDailyTasksCompletedOrNotCompleted(if (isCompleted) 1 else 0)

    fun getTotalDailyTasksCompletedOrNotCompleted(isCompleted: Boolean) =
        taskRepository.getTotalDailyTasksCompletedOrNotCompleted(if (isCompleted) 1 else 0)
}