package com.lutech.notepad.database.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.lutech.notepad.database.dao.CategoryNoteDao
import com.lutech.notepad.model.CategoryTaskCrossRef
import com.lutech.notepad.model.CategoryWithNotes
import com.lutech.notepad.model.NoteWithCategories

interface CategoryNoteRepository {
    suspend fun insertCategoryNote(item: List<CategoryTaskCrossRef>)

    fun getCategoryWithNote(id: Int): LiveData<List<CategoryWithNotes>>

    fun getNoteWithCategory(id: Int): LiveData<List<NoteWithCategories>>

    suspend fun delete(categoryTaskCrossRef: CategoryTaskCrossRef)
}

class CategoryNoteRepositoryImpl (
    val categoryNoteDao: CategoryNoteDao
) :CategoryNoteRepository {
    override suspend fun insertCategoryNote(item: List<CategoryTaskCrossRef>) {
        categoryNoteDao.insertCategoryNote(item)
    }

    override fun getCategoryWithNote(id: Int): LiveData<List<CategoryWithNotes>> {
        return categoryNoteDao.getCategoryWithNote(id)
    }


    override fun getNoteWithCategory(id: Int): LiveData<List<NoteWithCategories>> {
        return categoryNoteDao.getNoteWithCategory(id)
    }

    override suspend fun delete(categoryTaskCrossRef: CategoryTaskCrossRef) {
        categoryNoteDao.delete(categoryTaskCrossRef)
    }

}