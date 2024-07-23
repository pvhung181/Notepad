package com.lutech.notepad.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date

@Entity(
    tableName = "task"
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "task_id")
    var id : Int = 0,

    @ColumnInfo(name = "title")
    var title: String = "",

    @ColumnInfo(name = "content")
    var content: String = "",

    @ColumnInfo(name = "last_edit")
    var lastEdit: String = SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(Date())
) {

}