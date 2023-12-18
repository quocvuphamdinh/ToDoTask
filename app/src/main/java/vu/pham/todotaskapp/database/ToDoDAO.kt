package vu.pham.todotaskapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import vu.pham.todotaskapp.models.Task

@Dao
interface ToDoDAO {
    @Insert
    suspend fun insertTask(task: Task)

    @Query("SELECT * FROM task WHERE DATE(DATETIME(taskDate/1000, 'unixepoch', 'localtime')) = DATE('now') ORDER BY taskDate DESC, priority ASC LIMIT :size")
    fun getTodayTasksWithSize(size: Int): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE DATE(DATETIME(taskDate/1000, 'unixepoch', 'localtime')) = DATE('now') ORDER BY taskDate DESC, priority ASC")
    fun getAllTodayTasks(): Flow<List<Task>>

    @Query("SELECT COUNT(*) FROM task WHERE DATE(DATETIME(taskDate/1000, 'unixepoch', 'localtime')) = DATE('now') AND isCompleted = :isCompleted")
    fun getTotalTodayTasksCompletedOrNotCompleted(isCompleted: Int): Flow<Int>

    @Query("SELECT * FROM task WHERE isCompleted = :isCompleted AND isDailyTask = 1")
    fun getDailyTasksCompletedOrNotCompleted(isCompleted: Int): Flow<List<Task>>

    @Query("SELECT COUNT(*) FROM task WHERE isCompleted = :isCompleted AND isDailyTask = 1")
    fun getTotalDailyTasksCompletedOrNotCompleted(isCompleted: Int): Flow<Int>

    @Query("SELECT * FROM task WHERE DATE(DATETIME(taskDate/1000, 'unixepoch', 'localtime')) = DATE('now', '1 day') ORDER BY taskDate DESC, priority ASC LIMIT :size")
    fun getTomorrowTasksWithSize(size: Int): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE DATE(DATETIME(taskDate/1000, 'unixepoch', 'localtime')) = DATE('now', '1 day') ORDER BY taskDate DESC, priority ASC")
    fun getAllTomorrowTasks(): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE isDailyTask = 1 ORDER BY taskDate DESC, priority ASC LIMIT :size")
    fun getDailyTasksWithSize(size: Int): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE isDailyTask = 1 ORDER BY taskDate DESC, priority ASC")
    fun getAllDailyTasks(): Flow<List<Task>>
}