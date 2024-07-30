package com.lutech.notepad.database.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.lutech.notepad.model.CategoryTaskCrossRef
import com.lutech.notepad.model.CategoryWithNotes
import com.lutech.notepad.model.NoteWithCategories

@Dao
interface CategoryNoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoryNote(item: List<CategoryTaskCrossRef>)

    @Transaction
    @Query("select * from category where categoryId = :id")
    fun getCategoryWithNote(id: Int): LiveData<List<CategoryWithNotes>>

    @Transaction
    @Query("select * from task where taskId = :id")
    fun getNoteWithCategory(id: Int): LiveData<List<NoteWithCategories>>


    @Delete
    suspend fun delete(categoryTaskCrossRef: CategoryTaskCrossRef)
}