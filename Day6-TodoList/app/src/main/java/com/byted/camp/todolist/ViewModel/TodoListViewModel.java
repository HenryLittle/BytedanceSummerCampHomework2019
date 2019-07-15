package com.byted.camp.todolist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.byted.camp.todolist.Repository.TodoRepository;
import com.byted.camp.todolist.db.room.TodoEntity;

import java.util.List;

import io.reactivex.Completable;

public class TodoListViewModel extends AndroidViewModel {
    // Never pass context into ViewModel instances.
    // Do not store Activity, Fragment, or View instances or their Context in the ViewModel.
    private TodoRepository mRepository;


    private LiveData<List<TodoEntity>> allTodos;

    public TodoListViewModel(@NonNull Application application) {
        super(application);
        mRepository = new TodoRepository(application);
        allTodos = mRepository.getAllTodos();
    }

    public LiveData<List<TodoEntity>> getAllTodos() {
        return allTodos;
    }

    public Completable insert(TodoEntity todo) {
        return mRepository.insert(todo);
    }

    public Completable update(TodoEntity todo) {
        return mRepository.update(todo);
    }

    public Completable delete(TodoEntity todo) {
        return mRepository.delete(todo);
    }
}
