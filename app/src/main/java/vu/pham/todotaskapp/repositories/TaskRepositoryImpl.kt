package vu.pham.todotaskapp.repositories

import kotlinx.coroutines.flow.Flow
import vu.pham.todotaskapp.database.ToDoDAO
import vu.pham.todotaskapp.models.Task

class TaskRepositoryImpl(
    private val toDoDAO: ToDoDAO
) : TaskRepository {
    override suspend fun createTask(task: Task) = toDoDAO.insertTask(task)
    override suspend fun updateTask(task: Task) = toDoDAO.updateTask(task)
    override suspend fun deleteTask(task: Task) = toDoDAO.deleteTask(task)

    override fun getTodayTasks(size: Int): Flow<List<Task>> =
        if (size == 0) toDoDAO.getAllTodayTasks() else toDoDAO.getTodayTasksWithSize(size)

    override fun getTomorrowTasks(size: Int): Flow<List<Task>> =
        if (size == 0) toDoDAO.getAllTomorrowTasks() else toDoDAO.getTomorrowTasksWithSize(size)

    override fun getTotalTodayTasksCompletedOrNotCompleted(isCompleted: Int): Flow<Int> =
        toDoDAO.getTotalTodayTasksCompletedOrNotCompleted(isCompleted)

    override fun getDailyTasksCompletedOrNotCompleted(
        isCompleted: Int,
        size: Int
    ): Flow<List<Task>> =
        if (size == 0) toDoDAO.getDailyTasksCompletedOrNotCompleted(isCompleted)
        else toDoDAO.getDailyTasksCompletedOrNotCompletedWithSize(
            isCompleted,
            size
        )

    override fun getTotalDailyTasksCompletedOrNotCompleted(isCompleted: Int): Flow<Int> =
        toDoDAO.getTotalDailyTasksCompletedOrNotCompleted(isCompleted)

    override fun getDailyTasks(size: Int): Flow<List<Task>> =
        if (size == 0) toDoDAO.getAllDailyTasks() else toDoDAO.getDailyTasksWithSize(size)

    override fun getAllTasks(size: Int): Flow<List<Task>> =
        if (size == 0) toDoDAO.getAllTasks() else toDoDAO.getAllTasksWithSize(size)

    override fun getAllTasksByName(name: String): Flow<List<Task>> = toDoDAO.getAllTasksByName(name)
}