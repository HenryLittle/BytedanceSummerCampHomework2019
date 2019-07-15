package com.byted.camp.todolist.db.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.byted.camp.todolist.db.TodoContract;

import java.util.List;

import io.reactivex.Completable;

@Dao
public interface TodoDao {
    @Insert
    Completable insertTodo(TodoEntity todo);

    @Update
    Completable updateTodo(TodoEntity todo);

    @Delete
    Completable deleteTodo(TodoEntity todo);

    @Query("SELECT * FROM " + TodoContract.TodoNote.TABLE_NAME + " ORDER BY " + TodoContract.TodoNote.COLUMN_PRIORITY + " DESC")
    LiveData<List<TodoEntity>> getAllTodo();
}
