package com.lutech.notepad.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class Category (
    @ColumnInfo(name = "category_id")
    @PrimaryKey(autoGenerate = true)
    val categoryId: Int = 0,

    @ColumnInfo(name = "category_name")
    var categoryName: String
)