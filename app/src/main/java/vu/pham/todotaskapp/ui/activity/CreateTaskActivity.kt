package vu.pham.todotaskapp.ui.activity

import android.app.Activity
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vu.pham.todotaskapp.ui.theme.BackgroundColor
import vu.pham.todotaskapp.ui.theme.BlackLight
import vu.pham.todotaskapp.ui.theme.GreenLight
import vu.pham.todotaskapp.ui.theme.GreyLight
import vu.pham.todotaskapp.ui.theme.OrangeLight
import vu.pham.todotaskapp.ui.theme.PrimaryColor
import vu.pham.todotaskapp.ui.theme.TextColor
import vu.pham.todotaskapp.ui.theme.ToDoTaskAppTheme
import vu.pham.todotaskapp.ui.theme.WhiteColor
import vu.pham.todotaskapp.ui.theme.WhiteColor2
import vu.pham.todotaskapp.ui.utils.ToDoButton
import vu.pham.todotaskapp.ui.utils.ToDoCheckBox
import vu.pham.todotaskapp.ui.utils.ToDoDatePicker
import vu.pham.todotaskapp.ui.utils.ToDoTextField
import vu.pham.todotaskapp.ui.utils.timerPickerDialog
import vu.pham.todotaskapp.utils.DateUtils
import kotlin.time.Duration

class CreateTaskActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoTaskAppTheme {
                CreateTask()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CreateTask() {
    val context = LocalContext.current
    val activity = (context as? Activity)
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = BackgroundColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Create New Task")
                },
                navigationIcon = {
                    IconButton(onClick = { activity?.finish() }) {
                        Icon(Icons.Outlined.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = BackgroundColor,
                    navigationIconContentColor = TextColor,
                    titleContentColor = TextColor
                )
            )
        }
    ) { it ->
        CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(10.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                ToDoDatePicker(
                    modifier = Modifier.padding(bottom = 20.dp),
                    onDateSelected = { dateSelected ->

                    })
                Text(
                    text = "Schedule", fontSize = 20.sp, color = TextColor, modifier =
                    Modifier.padding(bottom = 10.dp)
                )
                ToDoTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    hintText = "Name",
                    leadingIcon = null,
                    onTextChanged = {

                    },
                    isLongText = false,
                    enabled = true,
                    value = null
                )

                ToDoTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    hintText = "Description",
                    leadingIcon = null,
                    onTextChanged = {

                    },
                    isLongText = true,
                    enabled = true,
                    value = null
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .weight(10f, true)
                            .padding(bottom = 10.dp),
                    ) {
                        var startTime by remember {
                            mutableStateOf(Calendar.getInstance())
                        }
                        Text(
                            text = "Start Time", fontSize = 20.sp, color = TextColor, modifier =
                            Modifier.padding(bottom = 10.dp)
                        )
                        ToDoTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                                .clickable {
                                    timerPickerDialog(
                                        context = context,
                                        onTimeSelected = { hour, minute ->
                                            val calendar = Calendar.getInstance()
                                            calendar.set(Calendar.HOUR_OF_DAY, hour)
                                            calendar.set(Calendar.MINUTE, minute)
                                            startTime = calendar
                                        }).show()
                                },
                            hintText = "00 : 00 AM",
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.AccessTime,
                                    contentDescription = null,
                                    tint = PrimaryColor
                                )
                            },
                            onTextChanged = {

                            },
                            isLongText = false,
                            enabled = false,
                            value = DateUtils.convertDateFormat(startTime.time, "hh : mm a")
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f, true))
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .weight(10f, true)
                            .padding(bottom = 10.dp),
                    ) {
                        var endTime by remember {
                            mutableStateOf(Calendar.getInstance())
                        }
                        Text(
                            text = "End Time", fontSize = 20.sp, color = TextColor, modifier =
                            Modifier.padding(bottom = 10.dp)
                        )
                        ToDoTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                                .clickable {
                                    timerPickerDialog(
                                        context = context,
                                        onTimeSelected = { hour, minute ->
                                            val calendar = Calendar.getInstance()
                                            calendar.set(Calendar.HOUR_OF_DAY, hour)
                                            calendar.set(Calendar.MINUTE, minute)
                                            endTime = calendar
                                        }).show()
                                },
                            hintText = "00 : 00 AM",
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.AccessTime,
                                    contentDescription = null,
                                    tint = PrimaryColor
                                )
                            },
                            onTextChanged = {

                            },
                            isLongText = false,
                            enabled = false,
                            value = DateUtils.convertDateFormat(endTime.time, "hh : mm a")
                        )
                    }
                }
                Text(
                    text = "Priority", fontSize = 20.sp, color = TextColor, modifier =
                    Modifier.padding(bottom = 10.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .border(
                                1.dp,
                                color = OrangeLight,
                                shape = RoundedCornerShape(5.dp)
                            )
                            .wrapContentSize()
                            .weight(10f, true)
                    ) {
                        Text(
                            text = "High",
                            fontSize = 16.sp,
                            color = TextColor,
                            modifier = Modifier
                                .background(BackgroundColor)
                                .padding(vertical = 10.dp)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f, true))
                    Surface(
                        modifier = Modifier
                            .border(
                                1.dp,
                                color = GreenLight,
                                shape = RoundedCornerShape(5.dp)
                            )
                            .wrapContentSize()
                            .weight(10f, true)
                    ) {
                        Text(
                            text = "Medium",
                            fontSize = 16.sp,
                            color = TextColor,
                            modifier = Modifier
                                .background(BackgroundColor)
                                .padding(vertical = 10.dp)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f, true))
                    Surface(
                        modifier = Modifier
                            .border(
                                1.dp,
                                color = GreyLight,
                                shape = RoundedCornerShape(5.dp)
                            )
                            .wrapContentSize()
                            .weight(10f, true)
                    ) {
                        Text(
                            text = "Low",
                            fontSize = 16.sp,
                            color = TextColor,
                            modifier = Modifier
                                .background(BackgroundColor)
                                .padding(vertical = 10.dp)
                        )
                    }
                }
                ToDoCheckBox(
                    modifier = Modifier,
                    textCheckBox = "Daily Task",
                    onCheckedChange = { isChecked ->

                    }
                )
                var isSwitchOn by remember {
                    mutableStateOf(false)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Get alert for this task", fontSize = 16.sp, color = TextColor)
                    Switch(
                        checked = isSwitchOn, onCheckedChange = { isOn ->
                            isSwitchOn = isOn
                        },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = PrimaryColor,
                            uncheckedTrackColor = WhiteColor2,
                            checkedThumbColor = WhiteColor,
                            uncheckedThumbColor = WhiteColor
                        )
                    )
                }
                ToDoButton(
                    buttonModifier = Modifier.fillMaxWidth(),
                    textButton = "Create Task",
                    textColor = TextColor,
                    onClick = {

                    }
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun CreatTaskPreview() {
    ToDoTaskAppTheme {
        CreateTask()
    }
}