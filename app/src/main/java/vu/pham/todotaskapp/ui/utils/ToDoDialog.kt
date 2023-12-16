package vu.pham.todotaskapp.ui.utils

import android.app.TimePickerDialog
import android.content.Context
import vu.pham.todotaskapp.R
import java.util.Calendar

fun timerPickerDialog(
    context: Context,
    onTimeSelected: (hour: Int, minute: Int) -> Unit
): TimePickerDialog {
    val calendar = Calendar.getInstance()
    val mHour = calendar[Calendar.HOUR_OF_DAY]
    val mMinute = calendar[Calendar.MINUTE]

    val timePickerDialog = TimePickerDialog(
        context,
        R.style.DatePickerTheme,
        { _, mHour: Int, mMinute: Int ->
            onTimeSelected(mHour, mMinute)
        }, mHour, mMinute, false
    )
    return timePickerDialog
}