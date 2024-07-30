package com.lutech.notepad.model

import android.text.format.DateUtils
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lutech.notepad.constants.TASK
import com.lutech.notepad.constants.TASK_DEFAULT_COLOR
import com.lutech.notepad.constants.TASK_DEFAULT_DARK_COLOR
import com.lutech.notepad.utils.formatDate
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date

@Entity(
    tableName = "task"
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    var taskId: Int = 0,

    var title: String = "",

    var content: String = "",

    var createDate: String = formatDate(Date()),

    var lastEdit: String = formatDate(Date()),

    var color: String = TASK_DEFAULT_COLOR,

    var darkColor: String = TASK_DEFAULT_DARK_COLOR,

    var isDeleted: Boolean = false
)