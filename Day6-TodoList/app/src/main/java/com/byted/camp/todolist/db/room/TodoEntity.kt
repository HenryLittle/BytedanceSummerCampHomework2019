package com.byted.camp.todolist.db.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

import com.byted.camp.todolist.db.beans.Priority
import com.byted.camp.todolist.db.beans.State
import com.byted.camp.todolist.db.TodoContract

import java.io.Serializable
import java.util.Date


@Entity(tableName = TodoContract.TodoNote.TABLE_NAME)
class TodoEntity(@field:ColumnInfo(name = TodoContract.TodoNote.COLUMN_DATE)
                 var date: Date?, @field:ColumnInfo(name = TodoContract.TodoNote.COLUMN_STATE)
                 var state: State?, @field:ColumnInfo(name = TodoContract.TodoNote.COLUMN_CONTENT)
                 var content: String?, @field:ColumnInfo(name = TodoContract.TodoNote.COLUMN_PRIORITY)
                 var priority: Priority?) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var _id: Long = 0


}
