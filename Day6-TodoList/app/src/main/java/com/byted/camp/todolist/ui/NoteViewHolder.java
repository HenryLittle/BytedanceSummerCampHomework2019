package com.byted.camp.todolist.ui;

import android.graphics.Color;
import android.graphics.Paint;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.byted.camp.todolist.MainActivity;
import com.byted.camp.todolist.NoteOperator;
import com.byted.camp.todolist.R;
import com.byted.camp.todolist.TodoListViewModel;
import com.byted.camp.todolist.TodoRepository;
import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.room.TodoEntity;

import java.text.SimpleDateFormat;
import java.util.Locale;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;

/**
 * Created on 2019/1/23.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public class NoteViewHolder extends RecyclerView.ViewHolder {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT =
            new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.ENGLISH);

    private final TodoListViewModel viewModel;

    private CheckBox checkBox;
    private TextView contentText;
    private TextView dateText;
    private View deleteBtn;

    public NoteViewHolder(@NonNull View itemView, TodoListViewModel viewModel) {
        super(itemView);
        this.viewModel = viewModel;

        checkBox = itemView.findViewById(R.id.checkbox);
        contentText = itemView.findViewById(R.id.text_content);
        dateText = itemView.findViewById(R.id.text_date);
        deleteBtn = itemView.findViewById(R.id.btn_delete);
    }

    public void bind(final TodoEntity note) {
        contentText.setText(note.getContent());
        dateText.setText(SIMPLE_DATE_FORMAT.format(note.getDate()));

        checkBox.setOnCheckedChangeListener(null);
        checkBox.setChecked(note.getState() == State.DONE);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                note.setState(isChecked ? State.DONE : State.TODO);
                Disposable d = viewModel.update(note).subscribe(new Action() {
                    @Override
                    public void run() {
                        Log.d("[BIND_LIVE]", "update success");
                    }
                });
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Disposable d = viewModel.delete(note).subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.d("[BIND_LIVE]", "delete success");
                    }
                });
            }
        });

        if (note.getState() == State.DONE) {
            contentText.setTextColor(Color.DKGRAY);
            contentText.setPaintFlags(contentText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            contentText.setTextColor(Color.BLACK);
            contentText.setPaintFlags(contentText.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }

        itemView.setBackgroundColor(note.getPriority().color);
    }
}
