package vu.pham.todotaskapp.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
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
) : Parcelable
