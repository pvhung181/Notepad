package com.lutech.notepad.model

import androidx.room.Entity
import androidx.room.ForeignKey


@Entity(
    primaryKeys = ["categoryId", "taskId"],
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["taskId"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class CategoryTaskCrossRef(
    var categoryId: Int,
    var taskId: Int
)