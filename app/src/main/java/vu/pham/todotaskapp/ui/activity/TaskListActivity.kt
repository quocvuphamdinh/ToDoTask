package vu.pham.todotaskapp.ui.activity

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vu.pham.todotaskapp.ToDoApplication
import vu.pham.todotaskapp.ui.theme.ToDoTaskAppTheme
import vu.pham.todotaskapp.ui.theme.BackgroundColor
import vu.pham.todotaskapp.ui.theme.TextColor
import vu.pham.todotaskapp.ui.utils.TaskItem
import vu.pham.todotaskapp.utils.TaskListType
import vu.pham.todotaskapp.viewmodels.TaskListViewModel
import vu.pham.todotaskapp.viewmodels.viewmodelfactory.viewModelFactory

class TaskListActivity : ComponentActivity() {
    private val taskListViewModel by viewModels<TaskListViewModel>(
        factoryProducer = {
            viewModelFactory {
                TaskListViewModel((application as ToDoApplication).taskRepository)
            }
        }
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoTaskAppTheme {
                val bundle = intent?.extras
                val title = bundle?.getString("title")
                val tasksType = bundle?.getSerializable("tasks_type") as TaskListType
                val context = LocalContext.current
                val activity = (context as? Activity)
                TaskList(activity, title ?: "Task List", tasksType, taskListViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskList(
    activity: Activity?,
    title: String,
    taskListType: TaskListType,
    viewModel: TaskListViewModel
) {
    val tasks = viewModel.getTasks(taskListType).collectAsStateWithLifecycle(initialValue = emptyList())
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
                .padding(start = 10.dp, end = 10.dp, top = 20.dp, bottom = 10.dp)
        ) {
            LazyColumn(content = {
                items(tasks.value.size){i->
                    TaskItem(task = tasks.value[i])
                }
            })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ToDoTaskAppTheme {

    }
}