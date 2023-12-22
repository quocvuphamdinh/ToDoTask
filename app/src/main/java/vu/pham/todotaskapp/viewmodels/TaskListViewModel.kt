package vu.pham.todotaskapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vu.pham.todotaskapp.models.Task
import vu.pham.todotaskapp.repositories.TaskRepository
import vu.pham.todotaskapp.utils.TaskListType

class TaskListViewModel(
    private val taskRepository: TaskRepository
): ViewModel() {
    fun getTasks(taskListType: TaskListType) =
        when(taskListType){
            TaskListType.DailyTasks -> taskRepository.getDailyTasks(0)
            TaskListType.TodayTasks -> taskRepository.getTodayTasks(0)
            TaskListType.TomorrowTasks -> taskRepository.getTomorrowTasks(0)
            TaskListType.AllTasks -> taskRepository.getAllTasks(0)
        }

    fun updateTask(task: Task) = viewModelScope.launch {
        taskRepository.updateTask(task)
    }
}