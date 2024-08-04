package com.lutech.notepad.listener

import android.view.View
import com.lutech.notepad.model.Task

interface NoteItemClickListener {
    fun setOnLongClickListener()
    fun setOnClickListener(task: Task)
    fun setOnClickInSelectedMode()
}