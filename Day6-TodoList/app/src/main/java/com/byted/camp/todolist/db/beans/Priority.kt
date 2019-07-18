package com.byted.camp.todolist.db.beans

import android.graphics.Color

/**
 * Created on 2019/1/23.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
enum class Priority private constructor(val intValue: Int, val color: Int) {
    High(2, Color.parseColor("#DDF5926E")),
    Medium(1, Color.parseColor("#DDB8F1C5")),
    Low(0, Color.WHITE);


    companion object {

        fun from(intValue: Int): Priority {
            for (priority in Priority.values()) {
                if (priority.intValue == intValue) {
                    return priority
                }
            }
            return Priority.Low // default
        }
    }
}
