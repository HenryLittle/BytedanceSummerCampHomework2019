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
                    + "(" + TodoNote.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + TodoNote.COLUMN_DATE + " INTEGER, "
                    + TodoNote.COLUMN_STATE + " INTEGER, "
                    + TodoNote.COLUMN_CONTENT + " TEXT, "
                    + TodoNote.COLUMN_PRIORITY + " INTEGER)")

    val SQL_ADD_PRIORITY_COLUMN =
            "ALTER TABLE " + TodoNote.TABLE_NAME + " ADD " + TodoNote.COLUMN_PRIORITY + " INTEGER"

    class TodoNote : BaseColumns {
        companion object {
            const val TABLE_NAME = "note"
            const val ID = "id"
            const val COLUMN_DATE = "date"
            const val COLUMN_STATE = "state"
            const val COLUMN_CONTENT = "content"
            const val COLUMN_PRIORITY = "priority"
        }
    }

}
