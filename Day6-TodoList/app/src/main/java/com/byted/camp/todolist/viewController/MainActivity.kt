package com.byted.camp.todolist.viewController

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.byted.camp.todolist.R
import com.byted.camp.todolist.viewModel.TodoListViewModel
import com.byted.camp.todolist.db.room.TodoEntity
import com.byted.camp.todolist.viewController.debug.DebugActivity
import com.byted.camp.todolist.viewController.adapters.NoteListAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

import io.reactivex.disposables.CompositeDisposable

class MainActivity : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null
    private var notesAdapter: NoteListAdapter? = null
    private var compositeDisposable = CompositeDisposable()


    private var todoListViewModel: TodoListViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            startActivityForResult(
                    Intent(this@MainActivity, NoteActivity::class.java),
                    REQUEST_CODE_ADD)
        }

        // acquire the ViewModel
        todoListViewModel = ViewModelProviders.of(this).get(TodoListViewModel::class.java)

        // init the recyclerView
        recyclerView = findViewById(R.id.list_todo)
        recyclerView!!.layoutManager = LinearLayoutManager(this,
                RecyclerView.VERTICAL, false)
        recyclerView!!.addItemDecoration(
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        notesAdapter = NoteListAdapter(todoListViewModel!!)
        recyclerView!!.adapter = notesAdapter

        todoListViewModel!!.allTodos.observe(this, Observer { todoEntities ->
            Log.d("[LIVE]", "" + todoEntities.size)
            notesAdapter!!.refresh(todoEntities)
        })
        // notesAdapter.refresh(loadNotesFromDatabase());
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.action_settings -> return true
            R.id.action_debug -> {
                startActivity(Intent(this, DebugActivity::class.java))
                return true
            }
            else -> {
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD && resultCode == Activity.RESULT_OK) {
            //notesAdapter.refresh(loadNotesFromDatabase());
            if (data != null) {
                val todo = data.getSerializableExtra(TODO_ITEM_EXTRA) as TodoEntity
                val d = todoListViewModel!!.insert(todo)
                        .subscribe { Toast.makeText(this@MainActivity, "Insert Success", Toast.LENGTH_SHORT).show() }
                compositeDisposable.add(d)
            } else {
                Toast.makeText(this@MainActivity,
                        "Error: Empty data", Toast.LENGTH_SHORT).show()
            }
        }
    }


    companion object {

        private const val REQUEST_CODE_ADD = 1002
        const val TODO_ITEM_EXTRA = "todo_item"
    }

}
