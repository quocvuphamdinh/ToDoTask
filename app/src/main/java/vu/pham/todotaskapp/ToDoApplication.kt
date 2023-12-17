package vu.pham.todotaskapp

import android.app.Application
import vu.pham.todotaskapp.database.ToDoDatabase
import vu.pham.todotaskapp.repositories.TaskRepository
import vu.pham.todotaskapp.repositories.TaskRepositoryImpl

class ToDoApplication: Application() {
    val database by lazy { ToDoDatabase.getInstance(this) }
    val taskRepository: TaskRepository by lazy { TaskRepositoryImpl(database.getToDoDAO()) }
}