package com.lutech.notepad.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.lutech.notepad.model.Task


@Dao
interface TaskDao {
    @Query("SELECT * FROM task where is_deleted = 0")
    fun getAll(): LiveData<MutableList<Task>>

    @Query("SELECT * FROM task where is_deleted = 1")
    fun getAllDeletedNotes(): LiveData<MutableList<Task>>

    @Query("select * from task order by task_id desc  limit 1")
    suspend fun getLastTask(): Task

    @Insert
    fun insertAll(vararg tasks: Task)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: Task): Long

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}