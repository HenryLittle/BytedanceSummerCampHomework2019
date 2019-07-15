package com.byted.camp.todolist;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.byted.camp.todolist.db.room.TodoDao;
import com.byted.camp.todolist.db.room.TodoEntity;
import com.byted.camp.todolist.db.room.TodoRoomDatabase;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TodoRepository {
    private TodoDao todoDao;

    public LiveData<List<TodoEntity>> getAllTodos() {
        return allTodos;
    }

    private LiveData<List<TodoEntity>> allTodos;

    TodoRepository(Application application) {
        TodoRoomDatabase database = TodoRoomDatabase.getDatabase(application);
        todoDao = database.todoDao();
        allTodos = todoDao.getAllTodo();
    }

    public Completable insert(TodoEntity todo) {
        return todoDao.insertTodo(todo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable update(TodoEntity todo) {
        return todoDao.updateTodo(todo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable delete(TodoEntity todo) {
        return todoDao.deleteTodo(todo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
