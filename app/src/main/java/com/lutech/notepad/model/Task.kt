package com.lutech.notepad.model

import android.text.format.DateUtils
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lutech.notepad.constants.TASK
import com.lutech.notepad.constants.TASK_DEFAULT_COLOR
import com.lutech.notepad.utils.formatDate
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

    @ColumnInfo(name = "creation_date")
    var createDate: String = formatDate(Date()),

    @ColumnInfo(name = "last_edit")
    var lastEdit: String = formatDate(Date()),

    @ColumnInfo(name = "color")
    var color: String = TASK_DEFAULT_COLOR,

    @ColumnInfo(name = "is_deleted")
    var isDeleted: Boolean = false
)