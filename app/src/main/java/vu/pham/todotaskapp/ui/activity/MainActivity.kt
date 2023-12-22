package vu.pham.todotaskapp.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vu.pham.todotaskapp.R
import vu.pham.todotaskapp.ToDoApplication
import vu.pham.todotaskapp.models.Task
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
import vu.pham.todotaskapp.ui.theme.WhiteColor3
import vu.pham.todotaskapp.ui.utils.TaskItem
import vu.pham.todotaskapp.ui.utils.ToDoFAB
import vu.pham.todotaskapp.ui.utils.ToDoProgressBar
import vu.pham.todotaskapp.ui.utils.ToDoTextField
import vu.pham.todotaskapp.utils.Constants
import vu.pham.todotaskapp.utils.DateUtils
import vu.pham.todotaskapp.utils.TaskListType
import vu.pham.todotaskapp.utils.width
import vu.pham.todotaskapp.viewmodels.HomeViewModel
import vu.pham.todotaskapp.viewmodels.viewmodelfactory.viewModelFactory
import java.util.Date

class MainActivity : ComponentActivity() {
    private val homeViewModel by viewModels<HomeViewModel>(
        factoryProducer = {
            viewModelFactory {
                HomeViewModel((application as ToDoApplication).taskRepository)
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoTaskAppTheme {
                MainPage(homeViewModel)
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainPage(
    homeViewModel: HomeViewModel
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
                            .verticalScroll(rememberScrollState())
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
                            onTextChanged = {

                            },
                            isLongText = false,
                            enabled = true,
                            value = null
                        )
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
                                        text = "You are almost done go ahead",
                                        color = WhiteColor3,
                                        fontSize = 12.sp
                                    )

                                    Text(
                                        text = "${progress}%", color = TextColor, fontSize = 16.sp,
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
                                        Intent(context, CreateTaskActivity::class.java).also {
                                            val bundle = Bundle()
                                            bundle.putParcelable("task", todayTasks.value[i])
                                            it.putExtras(bundle)
                                            context.startActivity(it)
                                        }
                                    },
                                        onTick = {
                                            val task = todayTasks.value[i]
                                            task.isCompleted = if (task.isCompleted == 1) 0 else 1
                                            homeViewModel.updateTask(task)
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
                                        Intent(context, CreateTaskActivity::class.java).also {
                                            val bundle = Bundle()
                                            bundle.putParcelable("task", tomorrowTasks.value[i])
                                            it.putExtras(bundle)
                                            context.startActivity(it)
                                        }
                                    },
                                        onTick = {
                                            val task = tomorrowTasks.value[i]
                                            task.isCompleted = if (task.isCompleted == 1) 0 else 1
                                            homeViewModel.updateTask(task)
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
                            if (tomorrowTasks.value.isNotEmpty()) {
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
                        if (tomorrowTasks.value.isEmpty()) {
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
                                        Intent(context, CreateTaskActivity::class.java).also {
                                            val bundle = Bundle()
                                            bundle.putParcelable("task", allTasks.value[i])
                                            it.putExtras(bundle)
                                            context.startActivity(it)
                                        }
                                    },
                                        onTick = {
                                            val task = allTasks.value[i]
                                            task.isCompleted = if (task.isCompleted == 1) 0 else 1
                                            homeViewModel.updateTask(task)
                                        })
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

fun goToTaskListPage(context: Context, title: String, tasksType: TaskListType) {
    Intent(context, TaskListActivity::class.java).also {
        val bundle = Bundle()
        bundle.putString("title", title)
        bundle.putSerializable("tasks_type", tasksType)
        it.putExtras(bundle)
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