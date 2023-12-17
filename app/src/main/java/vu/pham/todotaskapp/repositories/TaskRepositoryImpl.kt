package vu.pham.todotaskapp.repositories

import kotlinx.coroutines.flow.Flow
import vu.pham.todotaskapp.database.ToDoDAO
import vu.pham.todotaskapp.models.Task

class TaskRepositoryImpl(
    private val toDoDAO: ToDoDAO
) : TaskRepository {
    override suspend fun createTask(task: Task) = toDoDAO.insertTask(task)
    override fun getTodayTasks(size: Int): Flow<List<Task>> =
        if (size == 0) toDoDAO.getAllTodayTasks() else toDoDAO.getTodayTasksWithSize(size)

    override fun getTotalTodayTasksCompletedOrNotCompleted(isCompleted: Int): Flow<Int> =
        toDoDAO.getTotalTodayTasksCompletedOrNotCompleted(isCompleted)

    override fun getDailyTasksCompletedOrNotCompleted(isCompleted: Int): Flow<List<Task>> =
        toDoDAO.getDailyTasksCompletedOrNotCompleted(isCompleted)

    override fun getTotalDailyTasksCompletedOrNotCompleted(isCompleted: Int): Flow<Int> =
        toDoDAO.getTotalDailyTasksCompletedOrNotCompleted(isCompleted)
}