package com.byted.camp.todolist.viewController

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatRadioButton

import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast

import com.byted.camp.todolist.R
import com.byted.camp.todolist.db.beans.Priority
import com.byted.camp.todolist.db.beans.State
import com.byted.camp.todolist.db.room.TodoEntity

import java.util.Calendar


class NoteActivity : AppCompatActivity() {

    private var editText: EditText? = null
    private var addBtn: Button? = null
    private var radioGroup: RadioGroup? = null
    private var lowRadio: AppCompatRadioButton? = null


    private val selectedPriority: Priority
        get() {
            when (radioGroup!!.checkedRadioButtonId) {
                R.id.btn_high -> return Priority.High
                R.id.btn_medium -> return Priority.Medium
                else -> return Priority.Low
            }
        }

    //    private TodoDbHelper dbHelper;
    //    private SQLiteDatabase database;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        setTitle(R.string.take_a_note)

        //        dbHelper = new TodoDbHelper(this);
        //        database = dbHelper.getWritableDatabase();

        editText = findViewById(R.id.edit_text)
        editText!!.isFocusable = true
        editText!!.requestFocus()
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager?.showSoftInput(editText, 0)
        radioGroup = findViewById(R.id.radio_group)
        lowRadio = findViewById(R.id.btn_low)
        lowRadio!!.isChecked = true

        addBtn = findViewById(R.id.btn_add)

        addBtn!!.setOnClickListener(View.OnClickListener {
            val content = editText!!.text
            if (TextUtils.isEmpty(content)) {
                Toast.makeText(this@NoteActivity,
                        "No content to add", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            val intent = Intent()
            intent.putExtra(MainActivity.TODO_ITEM_EXTRA,
                    TodoEntity(Calendar.getInstance().time, State.TODO, content.toString(), selectedPriority))
            setResult(Activity.RESULT_OK, intent)
            finish()
            //                boolean succeed = saveNote2Database(content.toString().trim(),
            //                        getSelectedPriority());
            //                if (succeed) {
            //                    Toast.makeText(NoteActivity.this,
            //                            "Note added", Toast.LENGTH_SHORT).show();
            //                    setResult(Activity.RESULT_OK);
            //                } else {
            //                    Toast.makeText(NoteActivity.this,
            //                            "Error", Toast.LENGTH_SHORT).show();
            //                }
            //                finish();
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        //        database.close();
        //        database = null;
        //        dbHelper.close();
        //        dbHelper = null;
    }
}
