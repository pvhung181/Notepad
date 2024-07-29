package com.lutech.notepad.model

import androidx.room.Entity


@Entity(
    primaryKeys = ["categoryId", "taskId"]
)
class CategoryTaskCrossRef (
    var categoryId: Int,
    var taskId : Int
)