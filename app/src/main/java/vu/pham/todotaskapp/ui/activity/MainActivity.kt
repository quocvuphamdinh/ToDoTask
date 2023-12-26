package vu.pham.todotaskapp.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.util.Consumer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vu.pham.todotaskapp.R
import vu.pham.todotaskapp.ToDoApplication
import vu.pham.todotaskapp.alarm.AlarmItem
import vu.pham.todotaskapp.alarm.AndroidAlarmScheduler
import vu.pham.todotaskapp.models.Task
import vu.pham.todotaskapp.ui.theme.BackgroundColor
import vu.pham.todotaskapp.ui.theme.BlackLight
import vu.pham.todotaskapp.ui.theme.PrimaryColor
import vu.pham.todotaskapp.ui.theme.TextColor
import vu.pham.todotaskapp.ui.theme.ToDoTaskAppTheme
import vu.pham.todotaskapp.ui.theme.WhiteColor
import vu.pham.todotaskapp.ui.theme.WhiteColor2
import vu.pham.todotaskapp.ui.theme.WhiteColor3
import vu.pham.todotaskapp.ui.utils.TaskItem
import vu.pham.todotaskapp.ui.utils.ToDoFAB
import vu.pham.todotaskapp.ui.utils.ToDoProgressBar
import vu.pham.todotaskapp.ui.utils.ToDoTextField
import vu.pham.todotaskapp.utils.Constants
import vu.pham.todotaskapp.utils.ServiceActions
import vu.pham.todotaskapp.utils.TaskListType
import vu.pham.todotaskapp.utils.width
import vu.pham.todotaskapp.viewmodels.HomeViewModel
import vu.pham.todotaskapp.viewmodels.viewmodelfactory.viewModelFactory
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class MainActivity : ComponentActivity() {
    private val homeViewModel by viewModels<HomeViewModel>(
        factoryProducer = {
            viewModelFactory {
                HomeViewModel((application as ToDoApplication).taskRepository)
            }
        }
    )
    private val scheduler by lazy { (application as ToDoApplication).scheduler }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }
        setContent {
            ToDoTaskAppTheme {
                if (intent?.action == ServiceActions.SHOW_TASK.toString()) {
                    val bundle = intent?.extras
                    val task = bundle?.let {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            it.getParcelable("task", Task::class.java)
                        } else {
                            it.getParcelable("task") as Task?
                        }
                    }
                    task?.let {
                        goToTaskDetail(applicationContext, it)
                    }
                } else if (intent?.action == ServiceActions.NOTIFY_DAILY.toString()) {
                    Log.d("hivu", "zo daily task 1")
                    goToTaskListPage(
                        applicationContext,
                        "Daily Tasks",
                        TaskListType.DailyTasks
                    )
                }

                DisposableEffect(Unit) {
                    val listener = Consumer<Intent> { newIntent ->
                        Log.d("hivu", "onNewIntent")
                        if (newIntent?.action == ServiceActions.SHOW_TASK.toString()) {
                            val bundleNewIntent = newIntent.extras
                            val taskNewIntent = bundleNewIntent?.let {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    it.getParcelable("task", Task::class.java)
                                } else {
                                    it.getParcelable("task") as Task?
                                }
                            }
                            taskNewIntent?.let {
                                goToTaskDetail(applicationContext, it)
                            }
                        } else if (newIntent?.action == ServiceActions.NOTIFY_DAILY.toString()) {
                            Log.d("hivu", "zo daily task 2")
                            goToTaskListPage(
                                applicationContext,
                                "Daily Tasks",
                                TaskListType.DailyTasks
                            )
                        }
                    }
                    addOnNewIntentListener(listener)
                    onDispose {
                        removeOnNewIntentListener(listener)
                    }
                }
                MainPage(homeViewModel, scheduler)
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainPage(
    homeViewModel: HomeViewModel,
    scheduler: AndroidAlarmScheduler
) {
    val context = LocalContext.current
    val todayTasks =
        homeViewModel.getTodayTasks().collectAsStateWithLifecycle(initialValue = emptyList())
    val totalTodayTasksNotCompleted =
        homeViewModel.getTotalTodayTasksCompletedOrNotCompleted(false).collectAsStateWithLifecycle(
            initialValue = 0
        )
    val totalDailyTasksNotCompleted =
        homeViewModel.getTotalDailyTasksCompletedOrNotCompleted(false).collectAsStateWithLifecycle(
            initialValue = 0
        )
    val totalDailyTasksCompleted =
        homeViewModel.getTotalDailyTasksCompletedOrNotCompleted(true).collectAsStateWithLifecycle(
            initialValue = 0
        )
    val tomorrowTasks =
        homeViewModel.getTomorrowTasks().collectAsStateWithLifecycle(initialValue = emptyList())
    val allTasks =
        homeViewModel.getAllTasks().collectAsStateWithLifecycle(initialValue = emptyList())
    var progress by remember {
        mutableStateOf(0)
    }
    var textSearch by remember {
        mutableStateOf("")
    }
    val tasksByName = homeViewModel.getAllTasksByName(textSearch).collectAsStateWithLifecycle(
        initialValue = emptyList()
    )
    Scaffold(
        floatingActionButton = {
            ToDoFAB {
                Intent(context, CreateTaskActivity::class.java).also {
                    context.startActivity(it)
                }
            }
        },
        content = {
            CompositionLocalProvider(
                LocalOverscrollConfiguration provides null,
                content = {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(BackgroundColor)
                            .padding(start = 10.dp, end = 10.dp, top = 20.dp, bottom = 10.dp)
                            .thenIf(textSearch.isEmpty()) {
                                Modifier.verticalScroll(rememberScrollState())
                            }
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                        ) {
                            TextTitle(totalTodayTasksNotCompleted.value)
                            Image(
                                painterResource(id = R.drawable.ic_to_do_list),
                                contentDescription = null,
                                modifier = Modifier.size(50.dp)
                            )
                        }
                        ToDoTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp),
                            hintText = "Search Task Here", leadingIcon = {
                                Icon(
                                    Icons.Outlined.Search, contentDescription = null,
                                    tint = WhiteColor
                                )
                            },
                            onTextChanged = { search ->
                                textSearch = search
                            },
                            isLongText = false,
                            enabled = true,
                            value = null
                        )
                        if (textSearch.isNotEmpty()) {
                            if (tasksByName.value.isEmpty()) {
                                Text(
                                    text = "No task is found",
                                    fontSize = 16.sp,
                                    color = WhiteColor2,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 20.dp),
                                    textAlign = TextAlign.Center
                                )
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    content = {
                                        items(tasksByName.value.size) { index ->
                                            TaskItem(
                                                task = tasksByName.value[index],
                                                onClick = {
                                                    Intent(
                                                        context,
                                                        CreateTaskActivity::class.java
                                                    ).also {
                                                        goToTaskDetail(
                                                            context,
                                                            tasksByName.value[index]
                                                        )
                                                    }
                                                },
                                                onTick = {
                                                    completeTask(
                                                        tasksByName.value[index],
                                                        homeViewModel,
                                                        scheduler
                                                    )
                                                })
                                        }
                                    })
                            }
                        } else {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 20.dp, bottom = 16.dp)
                            ) {
                                Text(text = "Progress", fontSize = 20.sp, color = TextColor)
                                if ((totalDailyTasksCompleted.value + totalDailyTasksNotCompleted.value) > 0) {
                                    Text(text = "See All", fontSize = 16.sp, color = PrimaryColor,
                                        modifier = Modifier.clickable {
                                            goToTaskListPage(
                                                context,
                                                "Daily Tasks",
                                                TaskListType.DailyTasks
                                            )
                                        })
                                }
                            }
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentSize()
                                    .clickable {
                                        goToTaskListPage(
                                            context,
                                            "Daily Tasks",
                                            TaskListType.DailyTasks
                                        )
                                    },
                                color = BlackLight,
                                shape = RoundedCornerShape(5)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 20.dp)
                                ) {
                                    Text(
                                        text = "Daily Task", color = TextColor, fontSize = 16.sp,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                    Text(
                                        text = "${totalDailyTasksCompleted.value}/${totalDailyTasksCompleted.value + totalDailyTasksNotCompleted.value} Task Completed",
                                        color = WhiteColor2,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                    progress =
                                        if ((totalDailyTasksCompleted.value + totalDailyTasksNotCompleted.value) > 0)
                                            ((totalDailyTasksCompleted.value.toDouble() / (totalDailyTasksCompleted.value + totalDailyTasksNotCompleted.value)) * 100).toInt()
                                        else 0
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = if (progress >= 100) "Good job! Your daily tasks are completed"
                                            else if (progress in 50..99) "You are almost done go ahead!"
                                            else if (progress <= 0) "Please start to do your tasks or create it."
                                            else "Please remember to do your tasks.",
                                            color = WhiteColor3,
                                            fontSize = 12.sp
                                        )

                                        Text(
                                            text = "${progress}%",
                                            color = TextColor,
                                            fontSize = 16.sp,
                                        )
                                    }
                                    ToDoProgressBar(progress)
                                }
                            }
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 20.dp, bottom = 16.dp)
                            ) {
                                Text(text = "Today's Task", fontSize = 20.sp, color = TextColor)
                                if (todayTasks.value.isNotEmpty()) {
                                    Text(text = "See All", fontSize = 16.sp, color = PrimaryColor,
                                        modifier = Modifier.clickable {
                                            goToTaskListPage(
                                                context,
                                                "Today's Tasks",
                                                TaskListType.TodayTasks
                                            )
                                        })
                                }
                            }
                            if (todayTasks.value.isEmpty()) {
                                Text(
                                    text = "You don't have any today tasks to do",
                                    color = WhiteColor2,
                                    fontSize = 16.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 20.dp),
                                    textAlign = TextAlign.Center
                                )
                            } else {
                                Column {
                                    repeat(todayTasks.value.size) { i ->
                                        TaskItem(todayTasks.value[i], onClick = {
                                            Intent(
                                                context,
                                                CreateTaskActivity::class.java
                                            ).also {
                                                goToTaskDetail(context, todayTasks.value[i])
                                            }
                                        },
                                            onTick = {
                                                completeTask(
                                                    todayTasks.value[i],
                                                    homeViewModel,
                                                    scheduler
                                                )
                                            })
                                    }
                                }
                            }
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 20.dp, bottom = 16.dp)
                            ) {
                                Text(text = "Tomorrow's Task", fontSize = 20.sp, color = TextColor)
                                if (tomorrowTasks.value.isNotEmpty()) {
                                    Text(text = "See All", fontSize = 16.sp, color = PrimaryColor,
                                        modifier = Modifier.clickable {
                                            goToTaskListPage(
                                                context,
                                                "Tomorrow's Tasks",
                                                TaskListType.TomorrowTasks
                                            )
                                        })
                                }
                            }
                            if (tomorrowTasks.value.isEmpty()) {
                                Text(
                                    text = "You don't have any tomorrow tasks to do",
                                    color = WhiteColor2,
                                    fontSize = 16.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 20.dp),
                                    textAlign = TextAlign.Center
                                )
                            } else {
                                Column {
                                    repeat(tomorrowTasks.value.size) { i ->
                                        TaskItem(tomorrowTasks.value[i], onClick = {
                                            Intent(
                                                context,
                                                CreateTaskActivity::class.java
                                            ).also {
                                                goToTaskDetail(context, tomorrowTasks.value[i])
                                            }
                                        },
                                            onTick = {
                                                completeTask(
                                                    tomorrowTasks.value[i],
                                                    homeViewModel,
                                                    scheduler
                                                )
                                            })
                                    }
                                }
                            }
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 20.dp, bottom = 16.dp)
                            ) {
                                Text(text = "All Task", fontSize = 20.sp, color = TextColor)
                                if (allTasks.value.isNotEmpty()) {
                                    Text(text = "See All", fontSize = 16.sp, color = PrimaryColor,
                                        modifier = Modifier.clickable {
                                            goToTaskListPage(
                                                context,
                                                "All Tasks",
                                                TaskListType.AllTasks
                                            )
                                        })
                                }
                            }
                            if (allTasks.value.isEmpty()) {
                                Text(
                                    text = "You don't have any tasks",
                                    color = WhiteColor2,
                                    fontSize = 16.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 20.dp),
                                    textAlign = TextAlign.Center
                                )
                            } else {
                                Column {
                                    repeat(allTasks.value.size) { i ->
                                        TaskItem(allTasks.value[i], onClick = {
                                            goToTaskDetail(context, allTasks.value[i])
                                        },
                                            onTick = {
                                                completeTask(
                                                    allTasks.value[i],
                                                    homeViewModel,
                                                    scheduler
                                                )
                                            })
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }
    )
}

@Composable
fun TextTitle(taskNumber: Int) {
    val inlineContent = mapOf(
        Pair(
            "inlineContent",
            InlineTextContent(
                Placeholder(
                    width = 25.sp,
                    height = 25.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.AboveBaseline
                )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_pencil),
                    "",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(50.dp)
                )
            }
        )
    )
    val text = buildAnnotatedString {
        append("You have got $taskNumber tasks today to complete")
        appendInlineContent("inlineContent", "[icon]")
    }
    Text(
        text = text,
        inlineContent = inlineContent,
        modifier = Modifier.width((width() * 0.7).dp),
        color = TextColor,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        style = LocalTextStyle.current.copy(lineHeight = 35.sp)
    )
}

private inline fun Modifier.thenIf(condition: Boolean, block: () -> Modifier): Modifier {
    return if (condition) then(block()) else this
}

fun completeTask(completedTask: Task, viewModel: HomeViewModel, scheduler: AndroidAlarmScheduler) {
    val task =
        if (completedTask.isCompleted == 1) completedTask.copy(isCompleted = 0) else completedTask.copy(
            isCompleted = 1
        )
    viewModel.updateTask(task)
    if (task.isAlert == 1) {
        val alarmItem = AlarmItem(
            id = task.id!!,
            time = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(task.taskDate),
                ZoneId.systemDefault()
            ),
            title = Constants.ALARM_TITLE,
            message = Constants.alarmContent(task)
        )
        if (task.isCompleted == 1) {
            scheduler.cancel(alarmItem)
        } else {
            scheduler.schedule(alarmItem, task)
        }
    }
}

fun goToTaskDetail(context: Context, task: Task) {
    Intent(
        context,
        CreateTaskActivity::class.java
    ).also {
        val bundle = Bundle()
        bundle.putParcelable("task", task)
        it.putExtras(bundle)
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        context.startActivity(it)
    }
}

fun goToTaskListPage(context: Context, title: String, tasksType: TaskListType) {
    Intent(context, TaskListActivity::class.java).also {
        val bundle = Bundle()
        bundle.putString("title", title)
        bundle.putSerializable("tasks_type", tasksType)
        it.putExtras(bundle)
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        context.startActivity(it)
    }
}

@Preview(showBackground = true)
@Composable
fun ToDoPreview() {
    ToDoTaskAppTheme {
        //MainPage()
    }
}