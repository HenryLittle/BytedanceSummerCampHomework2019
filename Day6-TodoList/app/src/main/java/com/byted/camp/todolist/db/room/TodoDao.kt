package com.byted.camp.todolist.db.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

import com.byted.camp.todolist.db.TodoContract

import io.reactivex.Completable

@Dao
interface TodoDao {

    @get:Query("SELECT * FROM " + TodoContract.TodoNote.TABLE_NAME + " ORDER BY " + TodoContract.TodoNote.COLUMN_PRIORITY + " DESC")
    val allTodo: LiveData<List<TodoEntity>>

    @Insert
    fun insertTodo(todo: TodoEntity): Completable

    @Update
    fun updateTodo(todo: TodoEntity): Completable

    @Delete
    fun deleteTodo(todo: TodoEntity): Completable
}
