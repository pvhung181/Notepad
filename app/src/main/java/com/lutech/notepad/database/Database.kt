package com.lutech.notepad.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lutech.notepad.database.dao.CategoryDao
import com.lutech.notepad.database.dao.CategoryNoteDao
import com.lutech.notepad.database.dao.TaskDao
import com.lutech.notepad.model.Category
import com.lutech.notepad.model.CategoryTaskCrossRef
import com.lutech.notepad.model.Task

@Database(entities = [Task::class, Category::class, CategoryTaskCrossRef::class], version = 9)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao
    abstract fun categoryNoteDao(): CategoryNoteDao

    companion object {
        @Volatile
        private var Instance: AppDatabase ?= null

        fun getDatabase(context: Context): AppDatabase =
            Instance ?: synchronized(this) {
                Room.databaseBuilder(context = context, klass = AppDatabase::class.java, name = "todo_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also {Instance = it}
            }
    }
}