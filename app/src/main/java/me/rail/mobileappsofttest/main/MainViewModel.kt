package me.rail.mobileappsofttest.main

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.rail.mobileappsofttest.db.Note
import me.rail.mobileappsofttest.db.NotesDao
import me.rail.mobileappsofttest.db.NotesDatabase
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val notesDao: NotesDao) : ViewModel() {
    val notes = liveData {
        val allNotes = notesDao.getAll()
        allNotes.collect {
            emit(it)
        }
    }

    fun addNote(text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            notesDao.insert(Note(notesDao.getCount(), text, false))
        }
    }
}