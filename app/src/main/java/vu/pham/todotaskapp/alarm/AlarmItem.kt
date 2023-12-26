package vu.pham.todotaskapp.alarm

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.LocalDateTime

@Parcelize
data class AlarmItem(
    val id: Long,
    val time: LocalDateTime,
    val title: String,
    val message: String
) : Parcelable
