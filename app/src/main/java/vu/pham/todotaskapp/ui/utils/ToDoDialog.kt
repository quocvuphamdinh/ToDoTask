package vu.pham.todotaskapp.ui.utils

import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import vu.pham.todotaskapp.R
import vu.pham.todotaskapp.ui.theme.BlackColor
import vu.pham.todotaskapp.ui.theme.ButtonGreyColor
import vu.pham.todotaskapp.ui.theme.PrimaryColor
import vu.pham.todotaskapp.ui.theme.TextColor
import vu.pham.todotaskapp.ui.theme.WhiteColor
import vu.pham.todotaskapp.ui.theme.WhiteColor2
import vu.pham.todotaskapp.utils.width
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

@Composable
fun ToDoDialog(
    isShow: Boolean,
    title: String,
    content: String,
    onOK: () -> Unit,
    onCancel: () -> Unit
) {
    if (isShow) {
        Dialog(
            onDismissRequest = { },
            DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .wrapContentSize()
                    .width(width().dp)
                    .background(WhiteColor, shape = RoundedCornerShape(8.dp))
            ) {
                Column {
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextColor,
                        modifier = Modifier
                            .background(
                                PrimaryColor, RoundedCornerShape(
                                    topEnd = 8.dp,
                                    topStart = 8.dp
                                )
                            )
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = content,
                        fontSize = 16.sp,
                        color = BlackColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp)
                            .background(WhiteColor),
                        textAlign = TextAlign.Center
                    )
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                            .background(WhiteColor)
                    ) {
                        Text(
                            text = "Cancel", fontSize = 16.sp, fontWeight = FontWeight.Bold,
                            color = ButtonGreyColor,
                            modifier = Modifier
                                .clickable {
                                    onCancel()
                                }
                                .padding(end = 20.dp)
                        )
                        Text(text = "OK", fontSize = 16.sp, fontWeight = FontWeight.Bold,
                            color = PrimaryColor,
                            modifier = Modifier
                                .clickable {
                                    onOK()
                                })
                    }
                }
            }
        }
    }
}