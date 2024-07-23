package com.lutech.notepad

import android.app.Application
import com.lutech.notepad.database.AppDatabase
import com.lutech.notepad.database.repository.TaskRepositoryImpl

class NotepadApplication : Application() {
//        val database by lazy { AppDatabase.getDatabase(this) }
//        val repository by lazy { TaskRepositoryImpl(database.taskDao()) }
}