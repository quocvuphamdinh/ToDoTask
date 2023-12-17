package vu.pham.todotaskapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import vu.pham.todotaskapp.models.Task
import vu.pham.todotaskapp.repositories.TaskRepository
import vu.pham.todotaskapp.utils.AppState
import vu.pham.todotaskapp.utils.DateUtils
import java.util.Date

class CreateTaskViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _isCreateTaskSuccess =
        MutableSharedFlow<AppState>()
    val isCreateTaskSuccess: SharedFlow<AppState> = _isCreateTaskSuccess.asSharedFlow()

    private val _message =
        MutableSharedFlow<String>()
    val message: SharedFlow<String> = _message.asSharedFlow()

    fun createTask(task: Task) = viewModelScope.launch {
        if (task.name.isEmpty()) {
            _message.emit("Name must not be empty !")
            return@launch
        }
        if (task.taskDate == 0L) {
            _message.emit("Please select date for task !")
            return@launch
        }
        if (task.startTime == 0L) {
            _message.emit("Please select start time for task !")
            return@launch
        }
        if (task.endTime == 0L) {
            _message.emit("Please select end time for task !")
            return@launch
        }
        if (task.startTime >= task.endTime) {
            _message.emit("End time must be after start time !")
            return@launch
        }
        if (task.taskDate < System.currentTimeMillis()
            && DateUtils.convertDateFormat(
                Date(
                    task.taskDate
                ),
                "yyyy-MM-dd"
            ) != DateUtils.convertDateFormat(Date(System.currentTimeMillis()), "yyyy-MM-dd")
        ) {
            _message.emit("Task date must be greater than or equal now !")
            return@launch
        }
        if (task.priority == 0) {
            _message.emit("Please select priority for task !")
            return@launch
        }
        _isCreateTaskSuccess.emit(AppState.Loading)
        taskRepository.createTask(task)
        _isCreateTaskSuccess.emit(AppState.Success)
    }
}