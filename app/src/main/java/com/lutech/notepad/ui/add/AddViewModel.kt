package com.lutech.notepad.ui.add

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lutech.notepad.database.AppDatabase
import com.lutech.notepad.database.repository.TaskRepositoryImpl
import com.lutech.notepad.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: TaskRepositoryImpl = TaskRepositoryImpl(AppDatabase.getDatabase(application).taskDao())

    fun update(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

}