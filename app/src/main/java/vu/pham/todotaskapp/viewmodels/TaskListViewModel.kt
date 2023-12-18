package vu.pham.todotaskapp.viewmodels

import androidx.lifecycle.ViewModel
import vu.pham.todotaskapp.repositories.TaskRepository
import vu.pham.todotaskapp.utils.TaskListType

class TaskListViewModel(
    private val taskRepository: TaskRepository
): ViewModel() {

    fun getTasks(taskListType: TaskListType) =
        when(taskListType){
            TaskListType.DailyTasks -> taskRepository.getDailyTasksWithSize(0)
            TaskListType.TodayTasks -> taskRepository.getTodayTasks(0)
            TaskListType.TomorrowTasks -> taskRepository.getTomorrowTasks(0)
        }
}