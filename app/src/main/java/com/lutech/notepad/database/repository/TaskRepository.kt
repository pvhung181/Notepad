package com.lutech.notepad.database.repository

import androidx.lifecycle.LiveData
import com.lutech.notepad.database.dao.TaskDao
import com.lutech.notepad.model.Task
import kotlinx.coroutines.flow.Flow


interface TaskRepository {
    fun getAllTask(): LiveData<MutableList<Task>>

    fun getAllDeletedNotes(): LiveData<MutableList<Task>>

    //fun getAllTasksNotTracking(): List<Task>

    fun getLastTask(): Task


    suspend fun saveTask(task: Task): Long

    suspend fun deleteTask  (task: Task)

    suspend fun updateTask(task: Task)
//
//    suspend fun getTaskByDate(date: String): List<Task>
}

class TaskRepositoryImpl(
    val taskDao: TaskDao
) : TaskRepository {

    override fun getAllTask(): LiveData<MutableList<Task>> {
        return taskDao.getAll()
    }

    override fun getAllDeletedNotes(): LiveData<MutableList<Task>> {
        return taskDao.getAllDeletedNotes()
    }

    override fun getLastTask(): Task {
        return taskDao.getLastTask()
    }

//    override fun getAllTasksNotTracking(): List<Task> {
//        return taskDao.getAllTasksNotTracking()
//    }

    override suspend fun saveTask(task: Task): Long {
        return taskDao.insert(task)
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.delete(task)
    }

    override suspend fun updateTask(task: Task) {
        taskDao.update(task)
    }
//
//    override suspend fun getTaskByDate(date: String): List<Task> {
//        return taskDao.getTaskByDate(date)
//    }
}
