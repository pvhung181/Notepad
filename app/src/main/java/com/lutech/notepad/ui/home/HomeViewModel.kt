package com.lutech.notepad.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lutech.notepad.database.AppDatabase
import com.lutech.notepad.database.repository.CategoryNoteRepository
import com.lutech.notepad.database.repository.CategoryNoteRepositoryImpl
import com.lutech.notepad.database.repository.TaskRepositoryImpl
import com.lutech.notepad.model.CategoryWithNotes
import com.lutech.notepad.model.NoteWithCategories
import com.lutech.notepad.model.Task
import kotlinx.coroutines.launch

class HomeViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val repository: TaskRepositoryImpl = TaskRepositoryImpl(AppDatabase.getDatabase(application).taskDao())
    private val categoryNoteRepository: CategoryNoteRepository =
        CategoryNoteRepositoryImpl(AppDatabase.getDatabase(application).categoryNoteDao())

    private val _tasks = repository.getAllTask()

    val tasks: LiveData<MutableList<Task>> = _tasks


    //var categoryWithNotes: ? =

    fun getCategoryWithNotes(id: Int): LiveData<List<CategoryWithNotes>> {
        return categoryNoteRepository.getCategoryWithNote(id)
    }

    fun getNoteWithCategories(id: Int): LiveData<List<NoteWithCategories>> {
        return categoryNoteRepository.getNoteWithCategory(id)
    }
}