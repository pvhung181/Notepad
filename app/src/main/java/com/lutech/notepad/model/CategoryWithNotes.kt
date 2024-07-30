package com.lutech.notepad.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

class CategoryWithNotes(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "taskId",
        associateBy = Junction(CategoryTaskCrossRef::class)
    )
    val notes: List<Task>
)

