package com.byted.camp.todolist.db.beans

import java.util.Date

/**
 * Created on 2019/1/23.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
class Note(val id: Long) {
    var date: Date? = null
    var state: State? = null
    var content: String? = null
    var priority: Priority? = null
}
