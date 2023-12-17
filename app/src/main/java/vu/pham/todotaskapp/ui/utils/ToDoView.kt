package vu.pham.todotaskapp.ui.utils

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vu.pham.todotaskapp.ui.theme.BlackColor
import vu.pham.todotaskapp.ui.theme.BlackLight
import vu.pham.todotaskapp.ui.theme.PrimaryColor
import vu.pham.todotaskapp.ui.theme.PrimaryWithGreyColor
import vu.pham.todotaskapp.ui.theme.TextColor
import vu.pham.todotaskapp.ui.theme.WhiteColor
import vu.pham.todotaskapp.ui.theme.WhiteColor2
import vu.pham.todotaskapp.utils.DateUtils
import vu.pham.todotaskapp.utils.width
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoTextField(
    onTextChanged: (String) -> Unit,
    leadingIcon: @Composable (() -> Unit)?,
    hintText: String?,
    modifier: Modifier,
    isLongText: Boolean,
    enabled: Boolean,
    value: String?
) {
    var textValue by remember {
        mutableStateOf("")
    }
    val textStyle = if (isLongText) TextStyle(
        color = TextColor,
        fontSize = 18.sp
    ) else LocalTextStyle.current.copy(
        textAlign = TextAlign.Start, color = TextColor,
        fontSize = 18.sp
    )

    TextField(
        value = value ?: textValue,
        onValueChange = {
            textValue = it
            onTextChanged(textValue)
        },
        enabled = enabled,
        textStyle = textStyle,
        singleLine = !isLongText,
        modifier = if (isLongText) modifier.height(150.dp) else modifier,
        leadingIcon = leadingIcon,
        shape = RoundedCornerShape(if (isLongText) 10 else 20),
        colors = TextFieldDefaults.textFieldColors(
            cursorColor = WhiteColor,
            textColor = WhiteColor,
            disabledTextColor = WhiteColor,
            containerColor = BlackLight,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        placeholder = {
            Text(
                text = hintText ?: "", fontSize = 18.sp,
                color = WhiteColor2
            )
        },
    )
}

@Composable
fun ToDoProgressBar(progress: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(15.dp))
                .height(18.dp)
                .background(PrimaryWithGreyColor)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(15.dp))
                    .height(18.dp)
                    .background(PrimaryColor)
                    .width((width() * progress / 100).dp)
            )
        }

    }
}

@Composable
fun ToDoFAB(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = { onClick() },
        shape = CircleShape,
        containerColor = PrimaryColor
    ) {
        Icon(Icons.Filled.Add, "Create To Do Task", tint = BlackColor)
    }
}

@SuppressLint("SimpleDateFormat", "MutableCollectionMutableState")
@Composable
fun ToDoDatePicker(
    modifier: Modifier,
    onDateSelected: (date: Date) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        val currentDate = Date(System.currentTimeMillis())

        val currentCalendar = Calendar.getInstance()
        currentCalendar.time = currentDate
        currentCalendar.set(Calendar.HOUR_OF_DAY, 0)
        currentCalendar.set(Calendar.MINUTE, 0)
        currentCalendar.set(Calendar.SECOND, 0)


        val startDateOfCurrentDate = Calendar.getInstance()
        startDateOfCurrentDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        val dateEndOfYear = Calendar.getInstance()
        dateEndOfYear.set(Calendar.DATE, 31)
        dateEndOfYear.set(Calendar.MONTH, 11)
        dateEndOfYear.set(Calendar.HOUR_OF_DAY, 0)
        dateEndOfYear.set(Calendar.MINUTE, 0)
        dateEndOfYear.set(Calendar.SECOND, 0)

        val startDate by remember {
            val currentDate1 = Calendar.getInstance()
            currentDate1.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            currentDate1.set(Calendar.HOUR_OF_DAY, 0)
            currentDate1.set(Calendar.MINUTE, 0)
            currentDate1.set(Calendar.SECOND, 0)
            mutableStateOf(currentDate1)
        }
        val endDate by remember {
            val currentDate2 = Calendar.getInstance()
            currentDate2.time = startDate.time
            currentDate2.set(Calendar.HOUR_OF_DAY, 0)
            currentDate2.set(Calendar.MINUTE, 0)
            currentDate2.set(Calendar.SECOND, 0)
            currentDate2.add(Calendar.DATE, 6)
            mutableStateOf(currentDate2)
        }

        var dates by remember {
            mutableStateOf(
                DateUtils.getDates(
                    DateUtils.convertDateFormat(startDate.time, "yyyy-MM-dd"),
                    DateUtils.convertDateFormat(endDate.time, "yyyy-MM-dd")
                )
            )
        }

        var dateSelected by remember {
            mutableStateOf(
                DateUtils.convertDateFormat(
                    currentDate,
                    "yyyy-MM-dd"
                )
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = {
                if (startDate.time.after(startDateOfCurrentDate.time)) {
                    startDate.add(Calendar.DATE, -7)
                    endDate.add(Calendar.DATE, -7)
                    dates =
                        DateUtils.getDates(
                            DateUtils.convertDateFormat(startDate.time, "yyyy-MM-dd"),
                            DateUtils.convertDateFormat(endDate.time, "yyyy-MM-dd")
                        )
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowLeft,
                    contentDescription = null,
                    tint = PrimaryColor,
                    modifier = Modifier.size(50.dp)
                )
            }

            Text(
                text = "${
                    DateUtils.convertDateFormat(
                        startDate.time,
                        "dd"
                    )
                } ${
                    DateUtils.convertDateFormat(
                        startDate.time,
                        "MMMM"
                    ).substring(0, 3)
                } - ${DateUtils.convertDateFormat(endDate.time, "dd")} ${
                    DateUtils.convertDateFormat(
                        endDate.time,
                        "MMMM"
                    ).substring(0, 3)
                }", fontSize = 16.sp, color = PrimaryColor
            )
            IconButton(onClick = {
                if (endDate.time.before(dateEndOfYear.time)) {
                    startDate.add(Calendar.DATE, 7)
                    endDate.add(Calendar.DATE, 7)
                    dates =
                        DateUtils.getDates(
                            DateUtils.convertDateFormat(startDate.time, "yyyy-MM-dd"),
                            DateUtils.convertDateFormat(endDate.time, "yyyy-MM-dd")
                        )
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = PrimaryColor,
                    modifier = Modifier.size(50.dp)
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            repeat(dates.size) { i ->
                val dateItem = DateUtils.convertDateFormat(dates[i], "yyyy-MM-dd")
                val columnModifier =
                    if ((dateSelected == dateItem) && (dates[i] > currentCalendar.time || dateItem == DateUtils.convertDateFormat(
                            currentCalendar.time,
                            "yyyy-MM-dd"
                        ))
                    ) Modifier
                        .border(
                            2.dp,
                            color = PrimaryColor,
                            shape = RoundedCornerShape(5.dp)
                        )
                        .padding(5.dp) else Modifier.padding(5.dp)
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = columnModifier
                        .weight(1f, true)
                        .padding(5.dp)
                        .clickable {
                            if ((dates[i] > currentCalendar.time) || (dates[i] > currentCalendar.time || dateItem == DateUtils.convertDateFormat(
                                    currentCalendar.time,
                                    "yyyy-MM-dd"
                                ))
                            ) {
                                Log.d("hivu", dates[i].toString())
                                Log.d("hivu", currentCalendar.time.toString())
                                dateSelected = dateItem
                                onDateSelected(dates[i])
                            }
                        }
                ) {
                    Text(
                        text = DateUtils.convertDateFormat(dates[i], "EEEE")
                            .substring(0, 3),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 5.dp),
                        color = if (dateSelected == dateItem) PrimaryColor else if ((dates[i] < currentCalendar.time) && (dateItem != DateUtils.convertDateFormat(
                                currentCalendar.time,
                                "yyyy-MM-dd"
                            ))
                        ) WhiteColor2 else TextColor,
                        fontWeight = if (dateSelected == dateItem) FontWeight.Bold else null
                    )
                    Text(
                        text = DateUtils.convertDateFormat(dates[i], "dd"),
                        fontSize = 14.sp,
                        color = if (dateSelected == dateItem) PrimaryColor else if ((dates[i] < currentCalendar.time) && (dateItem != DateUtils.convertDateFormat(
                                currentCalendar.time,
                                "yyyy-MM-dd"
                            ))
                        ) WhiteColor2 else TextColor,
                        fontWeight = if (dateSelected == dateItem) FontWeight.Bold else null
                    )
                }
            }
        }
    }
}

@Composable
fun ToDoCheckBox(
    modifier: Modifier,
    onCheckedChange: (isChecked: Boolean) -> Unit,
    textCheckBox: String
) {
    var checked by remember {
        mutableStateOf(false)
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    )
    {
        Checkbox(
            checked = checked, onCheckedChange = { isChecked ->
                checked = isChecked
                onCheckedChange(checked)
            },
            colors = CheckboxDefaults.colors(
                checkedColor = PrimaryColor,
                uncheckedColor = PrimaryWithGreyColor
            ),
            modifier = modifier.absoluteOffset((-12).dp, 0.dp)
        )
        Text(text = textCheckBox, fontSize = 16.sp, color = TextColor)
    }
}

@Composable
fun ToDoButton(
    onClick: () -> Unit,
    buttonModifier: Modifier,
    textButton: String, textColor: Color?
) {
    val gradient =
        Brush.horizontalGradient(listOf(PrimaryColor, WhiteColor))
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        modifier = buttonModifier.background(gradient, shape = RoundedCornerShape(20))
    ) {
        Text(
            text = textButton, modifier = Modifier.padding(5.dp),
            fontSize = 16.sp,
            color = textColor ?: TextColor
        )
    }
}