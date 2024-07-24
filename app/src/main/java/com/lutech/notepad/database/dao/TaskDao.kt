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
    @Query("SELECT * FROM task")
    fun getAll(): LiveData<MutableList<Task>>

    @Insert
    fun insertAll(vararg tasks: Task)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}