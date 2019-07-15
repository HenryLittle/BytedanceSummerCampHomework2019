package com.byted.camp.todolist.db.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.byted.camp.todolist.beans.Priority;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract;

import java.io.Serializable;
import java.util.Date;


@Entity(tableName = TodoContract.TodoNote.TABLE_NAME)
public class TodoEntity implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long _id = 0;

    @ColumnInfo(name = TodoContract.TodoNote.COLUMN_DATE)
    private Date date;

    @ColumnInfo(name = TodoContract.TodoNote.COLUMN_STATE)
    private State state;

    @ColumnInfo(name = TodoContract.TodoNote.COLUMN_CONTENT)
    private String content;

    @ColumnInfo(name = TodoContract.TodoNote.COLUMN_PRIORITY)
    private Priority priority;

    public TodoEntity(Date date, State state, String content, Priority priority) {
        this.date = date;
        this.state = state;
        this.content = content;
        this.priority = priority;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }


}
