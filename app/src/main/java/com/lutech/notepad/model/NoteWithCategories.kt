package com.lutech.notepad.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

class NoteWithCategories(
    @Embedded val task: Task,
    @Relation(
        parentColumn = "taskId",
        entityColumn = "categoryId",
        associateBy = Junction(CategoryTaskCrossRef::class)
    )
    val categories: List<Category>
)