package com.byted.camp.todolist.db.room

import androidx.room.TypeConverter

import com.byted.camp.todolist.db.beans.Priority
import com.byted.camp.todolist.db.beans.State

import java.util.Date


class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun intToState(intVal: Int): State {
        return State.from(intVal)
    }

    @TypeConverter
    fun stateToInt(state: State): Int {
        return state.intValue
    }

    @TypeConverter
    fun intToPriority(intVal: Int): Priority {
        return Priority.from(intVal)
    }

    @TypeConverter
    fun priorityToInt(priority: Priority): Int {
        return priority.intValue
    }
}