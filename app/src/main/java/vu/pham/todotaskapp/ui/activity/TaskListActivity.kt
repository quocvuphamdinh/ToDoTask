package vu.pham.todotaskapp.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vu.pham.todotaskapp.ToDoApplication
import vu.pham.todotaskapp.alarm.AlarmItem
import vu.pham.todotaskapp.alarm.AndroidAlarmScheduler
import vu.pham.todotaskapp.ui.theme.ToDoTaskAppTheme
import vu.pham.todotaskapp.ui.theme.BackgroundColor
import vu.pham.todotaskapp.ui.theme.TextColor
import vu.pham.todotaskapp.ui.utils.TaskItem
import vu.pham.todotaskapp.utils.Constants
import vu.pham.todotaskapp.utils.DateUtils
import vu.pham.todotaskapp.utils.TaskListType
import vu.pham.todotaskapp.viewmodels.TaskListViewModel
import vu.pham.todotaskapp.viewmodels.viewmodelfactory.viewModelFactory
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

class TaskListActivity : ComponentActivity() {
    private val taskListViewModel by viewModels<TaskListViewModel>(
        factoryProducer = {
            viewModelFactory {
                TaskListViewModel((application as ToDoApplication).taskRepository)
            }
        }
    )
    private val scheduler by lazy { (application as ToDoApplication).scheduler }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoTaskAppTheme {
                val bundle = intent?.extras
                val title = bundle?.getString("title")
                val tasksType = bundle?.getSerializable("tasks_type") as TaskListType
                val context = LocalContext.current
                val activity = (context as? Activity)
                TaskList(
                    context,
                    activity,
                    title ?: "Task List",
                    tasksType,
                    taskListViewModel,
                    scheduler
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TaskList(
    context: Context,
    activity: Activity?,
    title: String,
    taskListType: TaskListType,
    viewModel: TaskListViewModel,
    scheduler: AndroidAlarmScheduler
) {
    val tasks =
        viewModel.getTasks(taskListType).collectAsStateWithLifecycle(initialValue = emptyList())
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = BackgroundColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = title)
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
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(start = 10.dp, end = 10.dp)
        ) {
            val listGroupBy = tasks.value.groupBy { task ->
                DateUtils.convertDateFormat(
                    Date(task.taskDate),
                    "yyyy"
                )
            }
            if (taskListType == TaskListType.AllTasks) {
                LazyColumn(
                    state = rememberLazyListState(),
                    content = {
                        listGroupBy.forEach { (year, list) ->
                            stickyHeader {
                                Text(
                                    text = year,
                                    fontSize = 16.sp,
                                    color = TextColor,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(BackgroundColor)
                                        .padding(vertical = 5.dp)
                                )
                            }
                            items(list.size, key = { listItem ->
                                listItem
                            }) { i ->
                                TaskItem(task = tasks.value[i], onClick = {
                                    Intent(
                                        context,
                                        CreateTaskActivity::class.java
                                    ).also { intent ->
                                        val bundle = Bundle()
                                        bundle.putParcelable("task", tasks.value[i])
                                        intent.putExtras(bundle)
                                        context.startActivity(intent)
                                    }
                                },
                                    onTick = {
                                        var task = tasks.value[i]
                                        task =
                                            if (task.isCompleted == 1) task.copy(isCompleted = 0) else task.copy(
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
                                    })
                            }
                        }
                    })
            } else {
                LazyColumn(
                    state = rememberLazyListState(),
                    content = {
                        items(tasks.value.size, key = { listItem ->
                            listItem
                        }) { i ->
                            TaskItem(task = tasks.value[i], onClick = {
                                Intent(context, CreateTaskActivity::class.java).also { intent ->
                                    val bundle = Bundle()
                                    bundle.putParcelable("task", tasks.value[i])
                                    intent.putExtras(bundle)
                                    context.startActivity(intent)
                                }
                            },
                                onTick = {
                                    var task = tasks.value[i]
                                    task =
                                        if (task.isCompleted == 1) task.copy(isCompleted = 0) else task.copy(
                                            isCompleted = 1
                                        )
                                    viewModel.updateTask(task)
                                })
                        }
                    })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ToDoTaskAppTheme {

    }
}