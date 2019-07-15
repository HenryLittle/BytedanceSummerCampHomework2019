package com.byted.camp.todolist.db

import android.provider.BaseColumns

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
object TodoContract {

    val SQL_CREATE_NOTES = (
            "CREATE TABLE " + TodoNote.TABLE_NAME
                    + "(" + TodoNote._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + TodoNote.COLUMN_DATE + " INTEGER, "
                    + TodoNote.COLUMN_STATE + " INTEGER, "
                    + TodoNote.COLUMN_CONTENT + " TEXT, "
                    + TodoNote.COLUMN_PRIORITY + " INTEGER)")

    val SQL_ADD_PRIORITY_COLUMN =
            "ALTER TABLE " + TodoNote.TABLE_NAME + " ADD " + TodoNote.COLUMN_PRIORITY + " INTEGER"

    class TodoNote : BaseColumns {
        companion object {
            val TABLE_NAME = "note"

            val COLUMN_DATE = "date"
            val COLUMN_STATE = "state"
            val COLUMN_CONTENT = "content"
            val COLUMN_PRIORITY = "priority"
        }
    }

}
