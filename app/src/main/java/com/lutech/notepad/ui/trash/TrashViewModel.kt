package com.lutech.notepad.ui.trash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.lutech.notepad.database.AppDatabase
import com.lutech.notepad.database.repository.TaskRepositoryImpl
import com.lutech.notepad.model.Task

class TrashViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val repository: TaskRepositoryImpl = TaskRepositoryImpl(AppDatabase.getDatabase(application).taskDao())

    private val _tasks = repository.getAllDeletedNotes()

    val tasks: LiveData<MutableList<Task>> = _tasks
}