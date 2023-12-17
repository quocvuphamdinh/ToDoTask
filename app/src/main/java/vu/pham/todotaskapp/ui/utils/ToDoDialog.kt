package vu.pham.todotaskapp.ui.utils

import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import vu.pham.todotaskapp.R
import vu.pham.todotaskapp.ui.theme.PrimaryColor
import vu.pham.todotaskapp.ui.theme.WhiteColor
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

@Composable
fun LoadingDialog(
    isShow: Boolean
) {
    if (isShow) {
        Dialog(
            onDismissRequest = { },
            DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .background(PrimaryColor, shape = RoundedCornerShape(8.dp))
            ) {
                CircularProgressIndicator(
                    color = WhiteColor
                )
            }
        }
    }
}