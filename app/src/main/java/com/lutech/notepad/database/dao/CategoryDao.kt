package com.lutech.notepad.database.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.lutech.notepad.model.Category

@Dao
interface CategoryDao {
    @Query("select * from category")
    fun getAllCategory(): LiveData<MutableList<Category>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCategory(category: Category)

    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)


}