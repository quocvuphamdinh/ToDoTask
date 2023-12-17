package vu.pham.todotaskapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task")
data class Task(
    @PrimaryKey(autoGenerate = true)
    var id: Long?,
    var name: String,
    var description: String,
    val createdDate: Long,
    val modifiedDate: Long?,
    var taskDate: Long,
    var startTime: Long,
    var endTime: Long,
    var priority: Int,
    var isDailyTask: Int,
    var isAlert: Int,
    var isCompleted: Int
)
