package me.rail.mobileappsofttest.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.rail.mobileappsofttest.db.Note
import me.rail.mobileappsofttest.db.NotesDao
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val notesDao: NotesDao) : ViewModel() {
    val notes = liveData {
        val allNotes = notesDao.getAll()
        allNotes.collect {
            emit(it)
            Log.d("notes", it.toString())
        }
    }

    fun addNote(text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            notesDao.insert(Note(notesDao.getCount(), text, false))
        }
    }

    fun setNoteToTop(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            notesDao.delete(note)
            for (i in note.id downTo 0) {
                notesDao.update(i)
            }
            notesDao.insert(note.copy(id = 0))
        }
    }
}