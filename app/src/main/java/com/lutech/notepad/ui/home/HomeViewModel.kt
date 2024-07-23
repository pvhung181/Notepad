package com.lutech.notepad.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lutech.notepad.database.AppDatabase
import com.lutech.notepad.database.repository.TaskRepositoryImpl
import com.lutech.notepad.model.Task

class HomeViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    private val repository: TaskRepositoryImpl = TaskRepositoryImpl(AppDatabase.getDatabase(application).taskDao())



    private val _tasks = repository.getAllTask()

    val tasks: LiveData<List<Task>> = _tasks
}