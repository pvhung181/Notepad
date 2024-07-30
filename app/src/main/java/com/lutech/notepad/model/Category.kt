package com.lutech.notepad.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class Category (

    @PrimaryKey(autoGenerate = true)
    val categoryId: Int = 0,
    var categoryName: String
)