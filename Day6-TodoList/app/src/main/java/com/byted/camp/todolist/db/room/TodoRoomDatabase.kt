package com.byted.camp.todolist.db.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

import com.byted.camp.todolist.db.beans.Priority
import com.byted.camp.todolist.db.beans.State

import java.util.Calendar

import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

@Database(entities = [TodoEntity::class], version = 1, exportSchema = false)
@TypeConverters(value = [Converters::class]) // add converters
abstract class TodoRoomDatabase : RoomDatabase() {

    abstract fun todoDao(): TodoDao

    companion object {

        private var INSTANCE: TodoRoomDatabase? = null

        fun getDatabase(context: Context): TodoRoomDatabase {
            if (INSTANCE == null) {
                synchronized(TodoRoomDatabase::class.java) {
                    if (INSTANCE == null) {
                        // Create database
                        INSTANCE = Room.databaseBuilder<TodoRoomDatabase>(context.applicationContext,
                                TodoRoomDatabase::class.java!!, "todo_list_database")
                                .fallbackToDestructiveMigrationOnDowngrade() // we just care about upgrade yet
                                //                            .fallbackToDestructiveMigration() // destructive upgrade
                                .addMigrations(MIGRATION_1_2)
                                .addCallback(populateCallback)
                                .build()
                    }
                }
            }
            return INSTANCE
        }

        private val populateCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // populate only when the database is created
                val d = Single.just(INSTANCE!!).subscribeOn(Schedulers.io())
                        .subscribe { todoRoomDatabase ->
                            todoRoomDatabase.todoDao().insertTodo(TodoEntity(Calendar.getInstance().time, State.from(0), "Some content 1", Priority.from(2))).subscribe()
                            todoRoomDatabase.todoDao().insertTodo(TodoEntity(Calendar.getInstance().time, State.from(0), "Some content 2", Priority.from(2))).subscribe()
                            todoRoomDatabase.todoDao().insertTodo(TodoEntity(Calendar.getInstance().time, State.from(0), "Some content 3", Priority.from(1))).subscribe()
                            todoRoomDatabase.todoDao().insertTodo(TodoEntity(Calendar.getInstance().time, State.from(0), "Some content 4", Priority.from(0))).subscribe()
                            todoRoomDatabase.todoDao().insertTodo(TodoEntity(Calendar.getInstance().time, State.from(0), "Some content 5", Priority.from(2))).subscribe()
                            todoRoomDatabase.todoDao().insertTodo(TodoEntity(Calendar.getInstance().time, State.from(0), "Some content 6", Priority.from(0))).subscribe()
                            todoRoomDatabase.todoDao().insertTodo(TodoEntity(Calendar.getInstance().time, State.from(0), "Some content 7", Priority.from(0))).subscribe()
                            todoRoomDatabase.todoDao().insertTodo(TodoEntity(Calendar.getInstance().time, State.from(0), "Some content 8", Priority.from(1))).subscribe()
                            todoRoomDatabase.todoDao().insertTodo(TodoEntity(Calendar.getInstance().time, State.from(0), "Some content 9", Priority.from(0))).subscribe()
                            todoRoomDatabase.todoDao().insertTodo(TodoEntity(Calendar.getInstance().time, State.from(0), "More content 1", Priority.from(0))).subscribe()
                            todoRoomDatabase.todoDao().insertTodo(TodoEntity(Calendar.getInstance().time, State.from(0), "More content 2", Priority.from(0))).subscribe()
                            todoRoomDatabase.todoDao().insertTodo(TodoEntity(Calendar.getInstance().time, State.from(0), "More content 3", Priority.from(0))).subscribe()
                            todoRoomDatabase.todoDao().insertTodo(TodoEntity(Calendar.getInstance().time, State.from(0), "More content 4", Priority.from(1))).subscribe()
                        }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(supportSQLiteDatabase: SupportSQLiteDatabase) {
                // do the migration job here
            }
        }
    }
}
