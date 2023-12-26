package vu.pham.todotaskapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import vu.pham.todotaskapp.alarm.AlarmItem
import vu.pham.todotaskapp.alarm.AndroidAlarmScheduler
import vu.pham.todotaskapp.models.Task
import vu.pham.todotaskapp.repositories.TaskRepository
import vu.pham.todotaskapp.utils.AppState
import vu.pham.todotaskapp.utils.Constants
import vu.pham.todotaskapp.utils.DateUtils
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
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

    private fun taskValidate(task: Task): String {
        if (task.name.isEmpty()) {
            return "Name must not be empty !"
        }
        if (task.taskDate == 0L) {
            return "Please select date for task !"
        }
        if (task.startTime == 0L) {
            return "Please select start time for task !"
        }
        if (task.endTime == 0L) {
            return "Please select end time for task !"
        }
        if (task.startTime >= task.endTime) {
            return "End time must be after start time !"
        }
        if (task.taskDate < System.currentTimeMillis()
            && DateUtils.convertDateFormat(
                Date(
                    task.taskDate
                ),
                "yyyy-MM-dd"
            ) != DateUtils.convertDateFormat(Date(System.currentTimeMillis()), "yyyy-MM-dd")
        ) {
            return "Task date must be greater than or equal now !"
        }
        if (task.priority == 0) {
            return "Please select priority for task !"
        }
        return ""
    }


    fun createTask(task: Task, scheduler: AndroidAlarmScheduler) = viewModelScope.launch {
        val validate = taskValidate(task)
        if (validate.isNotEmpty()) {
            _message.emit(validate)
            return@launch
        }
        _isCreateTaskSuccess.emit(AppState.Loading)
        val id = taskRepository.createTask(task)
        task.id = id
        if (task.isAlert == 1) {
            val alarmItem = AlarmItem(
                id = id,
                time = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(task.taskDate),
                    ZoneId.systemDefault()
                ),
                title = Constants.ALARM_TITLE,
                message = Constants.alarmContent(task)
            )
            scheduler.schedule(alarmItem, task)
        }
        _isCreateTaskSuccess.emit(AppState.Success)
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        val validate = taskValidate(task)
        if (validate.isNotEmpty()) {
            _message.emit(validate)
            return@launch
        }
        _isCreateTaskSuccess.emit(AppState.Loading)
        taskRepository.updateTask(task)
        _isCreateTaskSuccess.emit(AppState.Success)
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        _isCreateTaskSuccess.emit(AppState.Loading)
        taskRepository.deleteTask(task)
        _isCreateTaskSuccess.emit(AppState.Success)
    }
}