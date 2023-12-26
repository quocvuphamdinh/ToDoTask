package vu.pham.todotaskapp.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.util.Consumer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vu.pham.todotaskapp.ToDoApplication
import vu.pham.todotaskapp.alarm.AlarmItem
import vu.pham.todotaskapp.alarm.AndroidAlarmScheduler
import vu.pham.todotaskapp.models.Task
import vu.pham.todotaskapp.ui.theme.BackgroundColor
import vu.pham.todotaskapp.ui.theme.BlackColor
import vu.pham.todotaskapp.ui.theme.ButtonGreyColor
import vu.pham.todotaskapp.ui.theme.GreenLight
import vu.pham.todotaskapp.ui.theme.GreyLight
import vu.pham.todotaskapp.ui.theme.OrangeLight
import vu.pham.todotaskapp.ui.theme.PrimaryColor
import vu.pham.todotaskapp.ui.theme.TextColor
import vu.pham.todotaskapp.ui.theme.ToDoTaskAppTheme
import vu.pham.todotaskapp.ui.theme.WhiteColor
import vu.pham.todotaskapp.ui.theme.WhiteColor2
import vu.pham.todotaskapp.ui.utils.LoadingDialog
import vu.pham.todotaskapp.ui.utils.ToDoButton
import vu.pham.todotaskapp.ui.utils.ToDoCheckBox
import vu.pham.todotaskapp.ui.utils.ToDoDatePicker
import vu.pham.todotaskapp.ui.utils.ToDoDialog
import vu.pham.todotaskapp.ui.utils.ToDoTextField
import vu.pham.todotaskapp.ui.utils.timerPickerDialog
import vu.pham.todotaskapp.utils.AppState
import vu.pham.todotaskapp.utils.Constants
import vu.pham.todotaskapp.utils.DateUtils
import vu.pham.todotaskapp.utils.ServiceActions
import vu.pham.todotaskapp.viewmodels.CreateTaskViewModel
import vu.pham.todotaskapp.viewmodels.viewmodelfactory.viewModelFactory
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

class CreateTaskActivity : ComponentActivity() {
    private val createTaskViewModel by viewModels<CreateTaskViewModel>(
        factoryProducer = {
            viewModelFactory {
                CreateTaskViewModel((application as ToDoApplication).taskRepository)
            }
        }
    )
    private val scheduler by lazy { (application as ToDoApplication).scheduler }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoTaskAppTheme {
                val context = LocalContext.current
                val activity = (context as? Activity)

                LaunchedEffect(Unit) {
                    createTaskViewModel
                        .message
                        .collect { message ->
                            Toast.makeText(
                                context,
                                message,
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                }

                val createTaskState =
                    createTaskViewModel.isCreateTaskSuccess.collectAsStateWithLifecycle(AppState.Idle)
                when (createTaskState.value) {
                    AppState.Idle -> {}
                    AppState.Loading -> LoadingDialog(isShow = true)
                    AppState.Success -> {
                        LoadingDialog(isShow = false)
                        activity?.finish()
                    }

                    AppState.Error -> LoadingDialog(isShow = false)
                }

                val bundle = intent?.extras
                val task = bundle?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        it.getParcelable("task", Task::class.java)
                    } else {
                        it.getParcelable("task") as Task?
                    }
                }

                DisposableEffect(task) {
                    val listener = Consumer<Intent> { newIntent ->
                        if (newIntent?.action == ServiceActions.SHOW_TASK.toString()) {
                            activity?.finish()
                        }
                    }
                    addOnNewIntentListener(listener)
                    onDispose {
                        removeOnNewIntentListener(listener)
                    }
                }
                CreateTask(createTaskViewModel, context, activity, task = task, scheduler)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        Log.d("hivu", "onNewIntent2...")
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CreateTask(
    viewModel: CreateTaskViewModel,
    context: Context,
    activity: Activity?,
    task: Task?,
    scheduler: AndroidAlarmScheduler
) {
    var taskDate by remember {
        mutableStateOf(task?.taskDate ?: System.currentTimeMillis())
    }
    var name by remember {
        mutableStateOf(task?.name ?: "")
    }
    var description by remember {
        mutableStateOf(task?.description ?: "")
    }
    var isDailyTask by remember {
        mutableStateOf(
            if (task != null) {
                task.isDailyTask == 1
            } else false
        )
    }
    var startTime by remember {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.SECOND, 0)
        task?.let {
            calendar.time = Date(it.startTime)
        }
        mutableStateOf(calendar)
    }
    var endTime by remember {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.SECOND, 0)
        task?.let {
            calendar.time = Date(it.endTime)
        }
        mutableStateOf(calendar)
    }
    var priority by remember {
        mutableStateOf(task?.priority ?: 0)
    }
    var isSwitchOn by remember {
        mutableStateOf(
            if (task != null) {
                task.isAlert == 1
            } else false
        )
    }
    var isShowDialog by remember {
        mutableStateOf(false)
    }

    if (isShowDialog) {
        ToDoDialog(
            isShow = isShowDialog,
            title = "Are you sure?",
            content = "Do you want to delete this task ?",
            onOK = {
                isShowDialog = false
                if (task != null) {
                    viewModel.deleteTask(task)
                    val alarmItem = AlarmItem(
                        id = task.id!!,
                        time = LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(task.taskDate),
                            ZoneId.systemDefault()
                        ),
                        title = Constants.ALARM_TITLE,
                        message = Constants.alarmContent(task)
                    )
                    scheduler.cancel(alarmItem)
                }
            },
            onCancel = {
                isShowDialog = false
            }
        )
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = BackgroundColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = task?.name ?: "Create New Task")
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
                    initDate = if (task != null) Date(task.taskDate) else null,
                    modifier = Modifier.padding(bottom = 20.dp),
                    onDateSelected = { dateSelected ->
                        taskDate = dateSelected.time
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
                        name = it
                    },
                    isLongText = false,
                    enabled = true,
                    value = name
                )
                ToDoTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    hintText = "Description",
                    leadingIcon = null,
                    onTextChanged = {
                        description = it
                    },
                    isLongText = true,
                    enabled = true,
                    value = description
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
                                            calendar.set(Calendar.SECOND, 0)
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
                                            calendar.set(Calendar.SECOND, 0)
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
                            .weight(10f, true)
                            .clickable {
                                priority = Constants.HIGH_PRIORITY
                            }
                    ) {
                        Text(
                            textAlign = TextAlign.Center,
                            text = "High",
                            fontSize = 16.sp,
                            color = if (priority == Constants.HIGH_PRIORITY) BlackColor else TextColor,
                            modifier = Modifier
                                .background(
                                    if (priority == Constants.HIGH_PRIORITY) OrangeLight else BackgroundColor,
                                    shape = RoundedCornerShape(5.dp)
                                )
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
                            .weight(10f, true)
                            .clickable {
                                priority = Constants.MEDIUM_PRIORITY
                            }
                    ) {
                        Text(
                            textAlign = TextAlign.Center,
                            text = "Medium",
                            fontSize = 16.sp,
                            color = if (priority == Constants.MEDIUM_PRIORITY) BlackColor else TextColor,
                            modifier = Modifier
                                .background(
                                    if (priority == Constants.MEDIUM_PRIORITY) GreenLight else BackgroundColor,
                                    shape = RoundedCornerShape(5.dp)
                                )
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
                            .weight(10f, true)
                            .clickable {
                                priority = Constants.LOW_PRIORITY
                            }
                    ) {
                        Text(
                            textAlign = TextAlign.Center,
                            text = "Low",
                            fontSize = 16.sp,
                            color = if (priority == Constants.LOW_PRIORITY) BlackColor else TextColor,
                            modifier = Modifier
                                .background(
                                    if (priority == Constants.LOW_PRIORITY) GreyLight else BackgroundColor,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .padding(vertical = 10.dp)
                        )
                    }
                }
                ToDoCheckBox(
                    modifier = Modifier,
                    textCheckBox = "Daily Task",
                    onCheckedChange = { isChecked ->
                        isDailyTask = isChecked
                    },
                    initValueChecked = isDailyTask
                )
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
                if (task != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        ToDoButton(
                            onClick = {
                                if ((task.taskDate < System.currentTimeMillis()) && (DateUtils.convertDateFormat(
                                        Date(
                                            task.taskDate
                                        ),
                                        "yyyy-MM-dd"
                                    ) != DateUtils.convertDateFormat(
                                        Date(System.currentTimeMillis()),
                                        "yyyy-MM-dd"
                                    ))
                                ) {
                                    Toast.makeText(
                                        context,
                                        "You cannot edit the task because the task's date has passed today !",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    return@ToDoButton
                                }
                                if (task.isCompleted == 1) {
                                    Toast.makeText(
                                        context,
                                        "You cannot edit the task because the task's completed !",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    return@ToDoButton
                                }
                                val calendar = Calendar.getInstance()
                                calendar.time = Date(taskDate)
                                val calendar2 = Calendar.getInstance()
                                calendar2.time = Date(startTime.timeInMillis)
                                calendar.set(
                                    Calendar.HOUR_OF_DAY,
                                    calendar2.get(Calendar.HOUR_OF_DAY)
                                )
                                calendar.set(Calendar.MINUTE, calendar2.get(Calendar.MINUTE))
                                val taskUpdate = Task(
                                    id = task.id,
                                    name = name,
                                    description = description,
                                    createdDate = task.createdDate,
                                    modifiedDate = System.currentTimeMillis(),
                                    taskDate = calendar.timeInMillis,
                                    startTime = startTime.timeInMillis,
                                    endTime = endTime.timeInMillis,
                                    priority = priority,
                                    isDailyTask = if (isDailyTask) 1 else 0,
                                    isAlert = if (isSwitchOn) 1 else 0,
                                    isCompleted = task.isCompleted
                                )
                                viewModel.updateTask(taskUpdate)
                                val alarmItem = AlarmItem(
                                    id = taskUpdate.id!!,
                                    time = LocalDateTime.ofInstant(
                                        Instant.ofEpochMilli(taskUpdate.taskDate),
                                        ZoneId.systemDefault()
                                    ),
                                    title = Constants.ALARM_TITLE,
                                    message = Constants.alarmContent(taskUpdate)
                                )
                                if (taskUpdate.isAlert == 1) {
                                    scheduler.schedule(alarmItem, taskUpdate)
                                } else {
                                    scheduler.cancel(alarmItem)
                                }
                            },
                            buttonModifier = Modifier.weight(10f, true),
                            textButton = "Edit Task",
                            textColor = TextColor,
                            buttonColor = null
                        )
                        Spacer(modifier = Modifier.weight(1f, true))
                        ToDoButton(
                            onClick = {
                                isShowDialog = true
                            },
                            buttonModifier = Modifier.weight(10f, true),
                            textButton = "Delete Task",
                            textColor = TextColor,
                            buttonColor = ButtonGreyColor
                        )
                    }
                } else {
                    ToDoButton(
                        buttonModifier = Modifier.fillMaxWidth(),
                        textButton = "Create Task",
                        textColor = TextColor,
                        buttonColor = null,
                        onClick = {
                            val calendar = Calendar.getInstance()
                            calendar.time = Date(taskDate)
                            val calendar2 = Calendar.getInstance()
                            calendar2.time = Date(startTime.timeInMillis)
                            calendar.set(Calendar.HOUR_OF_DAY, calendar2.get(Calendar.HOUR_OF_DAY))
                            calendar.set(Calendar.MINUTE, calendar2.get(Calendar.MINUTE))
                            val taskResult = Task(
                                id = null,
                                name = name,
                                description = description,
                                createdDate = System.currentTimeMillis(),
                                modifiedDate = null,
                                taskDate = calendar.timeInMillis,
                                startTime = startTime.timeInMillis,
                                endTime = endTime.timeInMillis,
                                priority = priority,
                                isDailyTask = if (isDailyTask) 1 else 0,
                                isAlert = if (isSwitchOn) 1 else 0,
                                isCompleted = 0
                            )
                            viewModel.createTask(taskResult, scheduler)
                        }
                    )
                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun CreatTaskPreview() {
    ToDoTaskAppTheme {
        //CreateTask()
    }
}