package com.lutech.notepad.ui.trash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lutech.notepad.database.AppDatabase
import com.lutech.notepad.database.repository.TaskRepositoryImpl
import com.lutech.notepad.model.Task
import kotlinx.coroutines.launch

class TrashViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val repository: TaskRepositoryImpl = TaskRepositoryImpl(AppDatabase.getDatabase(application).taskDao())

    private val _tasks = repository.getAllDeletedNotes()

    val tasks: LiveData<MutableList<Task>> = _tasks

    fun restoreAll() {
        viewModelScope.launch {
            tasks.value?.forEach {
                repository.updateTask(it.copy(isDeleted = false))
            }
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            tasks.value?.forEach {
                repository.deleteTask(it)
            }
        }
    }
}