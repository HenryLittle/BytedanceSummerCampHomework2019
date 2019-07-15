package com.byted.camp.todolist.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

import com.byted.camp.todolist.Repository.TodoRepository
import com.byted.camp.todolist.db.room.TodoEntity

import io.reactivex.Completable

class TodoListViewModel(application: Application) : AndroidViewModel(application) {
    // Never pass context into ViewModel instances.
    // Do not store Activity, Fragment, or View instances or their Context in the ViewModel.
    private val mRepository: TodoRepository


    val allTodos: LiveData<List<TodoEntity>>

    init {
        mRepository = TodoRepository(application)
        allTodos = mRepository.allTodos
    }

    fun insert(todo: TodoEntity): Completable {
        return mRepository.insert(todo)
    }

    fun update(todo: TodoEntity): Completable {
        return mRepository.update(todo)
    }

    fun delete(todo: TodoEntity): Completable {
        return mRepository.delete(todo)
    }
}
