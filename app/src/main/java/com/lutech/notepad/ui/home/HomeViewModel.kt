package com.lutech.notepad.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lutech.notepad.database.AppDatabase
import com.lutech.notepad.database.repository.TaskRepositoryImpl
import com.lutech.notepad.model.Task
import kotlinx.coroutines.launch

class HomeViewModel(
    application: Application
) : AndroidViewModel(application) {

    private var mutableLiveData = MutableLiveData<String>()

    private val repository: TaskRepositoryImpl = TaskRepositoryImpl(AppDatabase.getDatabase(application).taskDao())

    private val _tasks = repository.getAllTask()

    val tasks: LiveData<MutableList<Task>> = _tasks


    fun setText(s: String) {
        mutableLiveData.value = s
    }

    fun getText(): MutableLiveData<String> {
        return mutableLiveData
    }

    fun moveToTrash(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task.copy(
                isDeleted = true
            ))
        }
    }

    fun restoreTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(
                task.copy(isDeleted = false)
            )
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

}