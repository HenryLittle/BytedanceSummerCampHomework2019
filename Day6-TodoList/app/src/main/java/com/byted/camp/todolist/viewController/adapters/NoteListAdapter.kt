package com.byted.camp.todolist.viewController.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup


import com.byted.camp.todolist.R
import com.byted.camp.todolist.viewModel.TodoListViewModel
import com.byted.camp.todolist.db.room.TodoEntity

import java.util.ArrayList

/**
 * Created on 2019/1/23.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
class NoteListAdapter(private val viewModel: TodoListViewModel) : RecyclerView.Adapter<NoteViewHolder>() {
    private val notes = ArrayList<TodoEntity>()

    fun refresh(newNotes: List<TodoEntity>?) {
        notes.clear()
        if (newNotes != null) {
            notes.addAll(newNotes)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, pos: Int): NoteViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(itemView, viewModel)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, pos: Int) {
        holder.bind(notes[pos])
    }

    override fun getItemCount(): Int {
        return notes.size
    }
}
