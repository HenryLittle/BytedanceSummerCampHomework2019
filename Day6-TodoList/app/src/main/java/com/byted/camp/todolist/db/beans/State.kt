package com.byted.camp.todolist.db.beans

/**
 * Created on 2019/1/23.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
enum class State private constructor(val intValue: Int) {
    TODO(0), DONE(1);


    companion object {

        fun from(intValue: Int): State {
            for (state in State.values()) {
                if (state.intValue == intValue) {
                    return state
                }
            }
            return TODO // default
        }
    }
}
