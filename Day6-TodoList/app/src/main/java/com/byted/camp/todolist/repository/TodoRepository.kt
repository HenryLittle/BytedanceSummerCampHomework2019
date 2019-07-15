package com.byted.camp.todolist.repository

import android.app.Application

import androidx.lifecycle.LiveData

import com.byted.camp.todolist.db.room.TodoDao
import com.byted.camp.todolist.db.room.TodoEntity
import com.byted.camp.todolist.db.room.TodoRoomDatabase

import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class TodoRepository(application: Application) {
    private val todoDao: TodoDao

    val allTodos: LiveData<List<TodoEntity>>

    init {
        val database = TodoRoomDatabase.getDatabase(application)
        todoDao = database.todoDao()
        allTodos = todoDao.allTodo
    }

    fun insert(todo: TodoEntity): Completable {
        return todoDao.insertTodo(todo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun update(todo: TodoEntity): Completable {
        return todoDao.updateTodo(todo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun delete(todo: TodoEntity): Completable {
        return todoDao.deleteTodo(todo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}
