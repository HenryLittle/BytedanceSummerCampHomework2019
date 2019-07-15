package com.byted.camp.todolist.db.room;

import androidx.room.TypeConverter;

import com.byted.camp.todolist.beans.Priority;
import com.byted.camp.todolist.beans.State;

import java.util.Date;


public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static State intToState(int intVal) {
        return State.from(intVal);
    }

    @TypeConverter
    public static int stateToInt(State state) {
        return state.intValue;
    }

    @TypeConverter
    public static Priority intToPriority(int intVal) {
        return Priority.from(intVal);
    }

    @TypeConverter
    public static int priorityToInt(Priority priority) {
        return priority.intValue;
    }
}