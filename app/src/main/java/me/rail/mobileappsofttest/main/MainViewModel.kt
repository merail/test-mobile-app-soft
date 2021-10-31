package me.rail.mobileappsofttest.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.rail.mobileappsofttest.db.Note
import me.rail.mobileappsofttest.db.NotesDao
import me.rail.mobileappsofttest.db.NotesDatabase
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val notesDao: NotesDao) : ViewModel() {
    val notes = liveData {
        val allNotes = notesDao.getAll()
        allNotes.collect {
            emit(it)
        }
    }

    fun addNote(context: Context, text: String) {
        viewModelScope.launch {
            val db = Room.databaseBuilder(context, NotesDatabase::class.java, "notes").build()
            db.notesDao().insert(Note(0, text, false))
        }
    }
}