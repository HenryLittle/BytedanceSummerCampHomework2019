package com.byted.camp.todolist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.db.room.TodoEntity;
import com.byted.camp.todolist.debug.DebugActivity;
import com.byted.camp.todolist.ui.NoteListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD = 1002;
    public static final String TODO_ITEM_EXTRA = "todo_item";

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

//    private TodoDbHelper dbHelper;
//    private SQLiteDatabase database;

    private TodoListViewModel todoListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(MainActivity.this, NoteActivity.class),
                        REQUEST_CODE_ADD);
            }
        });

//        dbHelper = new TodoDbHelper(this);
//        database = dbHelper.getWritableDatabase();

        // acquire the ViewModel
        todoListViewModel = ViewModelProviders.of(this).get(TodoListViewModel.class);

        // init the recyclerView
        recyclerView = findViewById(R.id.list_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                RecyclerView.VERTICAL, false));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notesAdapter = new NoteListAdapter(todoListViewModel);
        recyclerView.setAdapter(notesAdapter);

        todoListViewModel.getAllTodos().observe(this, new Observer<List<TodoEntity>>() {
            @Override
            public void onChanged(List<TodoEntity> todoEntities) {
                Log.d("[LIVE]", "" + todoEntities.size());
                notesAdapter.refresh(todoEntities);
            }
        });
        // notesAdapter.refresh(loadNotesFromDatabase());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
//        database.close();
//        database = null;
//        dbHelper.close();
//        dbHelper = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_debug:
                startActivity(new Intent(this, DebugActivity.class));
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD
                && resultCode == Activity.RESULT_OK) {
            //notesAdapter.refresh(loadNotesFromDatabase());
            if (data != null) {
                TodoEntity todo = (TodoEntity) data.getSerializableExtra(TODO_ITEM_EXTRA);
                Disposable d = todoListViewModel.insert(todo)
                        .subscribe(new Action() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Insert Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                compositeDisposable.add(d);
            } else {
                Toast.makeText(MainActivity.this,
                            "Error: Empty data", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Deprecated
    private void deleteNote(Note note) {
//        if (database == null) {
//            return;
//        }
//        int rows = database.delete(TodoNote.TABLE_NAME,
//                TodoNote._ID + "=?",
//                new String[]{String.valueOf(note.id)});
//        if (rows > 0) {
//            // notesAdapter.refresh(loadNotesFromDatabase());
//        }
    }

    @Deprecated
    private void updateNode(Note note) {
//        if (database == null) {
//            return;
//        }
//        ContentValues values = new ContentValues();
//        values.put(TodoNote.COLUMN_STATE, note.getState().intValue);
//
//        int rows = database.update(TodoNote.TABLE_NAME, values,
//                TodoNote._ID + "=?",
//                new String[]{String.valueOf(note.id)});
//        if (rows > 0) {
//            // notesAdapter.refresh(loadNotesFromDatabase());
//        }
    }

}
