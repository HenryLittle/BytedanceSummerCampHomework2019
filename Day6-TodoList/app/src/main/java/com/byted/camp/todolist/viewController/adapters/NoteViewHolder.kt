package com.byted.camp.todolist.ViewController.Adapters

import android.graphics.Color
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView

import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView

import com.byted.camp.todolist.R
import com.byted.camp.todolist.ViewModel.TodoListViewModel
import com.byted.camp.todolist.db.beans.State
import com.byted.camp.todolist.db.room.TodoEntity

import java.text.SimpleDateFormat
import java.util.Locale

import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action

/**
 * Created on 2019/1/23.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
class NoteViewHolder(itemView: View, private val viewModel: TodoListViewModel) : RecyclerView.ViewHolder(itemView) {

    private val checkBox: CheckBox
    private val contentText: TextView
    private val dateText: TextView
    private val deleteBtn: View

    init {

        checkBox = itemView.findViewById(R.id.checkbox)
        contentText = itemView.findViewById(R.id.text_content)
        dateText = itemView.findViewById(R.id.text_date)
        deleteBtn = itemView.findViewById(R.id.btn_delete)
    }

    fun bind(note: TodoEntity) {
        contentText.text = note.content
        dateText.text = SIMPLE_DATE_FORMAT.format(note.date)

        checkBox.setOnCheckedChangeListener(null)
        checkBox.isChecked = note.state === State.DONE
        checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            note.state = if (isChecked) State.DONE else State.TODO
            val d = viewModel.update(note).subscribe { Log.d("[BIND_LIVE]", "update success") }
        }
        deleteBtn.setOnClickListener { val d = viewModel.delete(note).subscribe { Log.d("[BIND_LIVE]", "delete success") } }

        if (note.state === State.DONE) {
            contentText.setTextColor(Color.DKGRAY)
            contentText.paintFlags = contentText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            contentText.setTextColor(Color.BLACK)
            contentText.paintFlags = contentText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        itemView.setBackgroundColor(note.priority!!.color)
    }

    companion object {

        private val SIMPLE_DATE_FORMAT = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.ENGLISH)
    }
}
