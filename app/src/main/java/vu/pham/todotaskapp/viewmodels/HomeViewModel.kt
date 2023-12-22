package vu.pham.todotaskapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vu.pham.todotaskapp.models.Task
import vu.pham.todotaskapp.repositories.TaskRepository

class HomeViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _progress = MutableStateFlow(0)
    val progress = _progress.asStateFlow()

    fun setProgress(completed: Int, total: Int) {
        Log.d("hivu", "$completed $total")
        _progress.value = (completed.div(total)) * 100
    }
    fun getTodayTasks() = taskRepository.getTodayTasks(3)

    fun getTotalTodayTasksCompletedOrNotCompleted(isCompleted: Boolean) =
        taskRepository.getTotalTodayTasksCompletedOrNotCompleted(if (isCompleted) 1 else 0)

    fun getDailyTasksCompletedOrNotCompleted(isCompleted: Boolean) =
        taskRepository.getDailyTasksCompletedOrNotCompleted(if (isCompleted) 1 else 0)

    fun getTotalDailyTasksCompletedOrNotCompleted(isCompleted: Boolean) =
        taskRepository.getTotalDailyTasksCompletedOrNotCompleted(if (isCompleted) 1 else 0)

    fun getTomorrowTasks() = taskRepository.getTomorrowTasks(3)

    fun getAllTasks() = taskRepository.getAllTasks(3)

    fun updateTask(task: Task) = viewModelScope.launch {
        taskRepository.updateTask(task)
    }
}