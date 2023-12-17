package vu.pham.todotaskapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import vu.pham.todotaskapp.models.Task
import vu.pham.todotaskapp.utils.Constants

@Database(
    entities = [Task::class],
    version = 1
)
abstract class ToDoDatabase: RoomDatabase() {
    companion object{
        @Volatile
        var instanceDb: ToDoDatabase? = null

        fun getInstance(context: Context): ToDoDatabase {
            return instanceDb ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ToDoDatabase::class.java,
                    Constants.DATABASE_NAME
                ).build()
                instanceDb = instance
                instance
            }
        }
    }

    abstract fun getToDoDAO() : ToDoDAO
}