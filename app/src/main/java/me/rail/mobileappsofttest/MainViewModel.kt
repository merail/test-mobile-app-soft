package me.rail.mobileappsofttest

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.rail.mobileappsofttest.db.CachedNote
import me.rail.mobileappsofttest.db.NotesDatabase

class MainViewModel : ViewModel() {
    fun getAllNotes(context: Context) {
        viewModelScope.launch(Dispatchers.Main) {
            val db = Room.databaseBuilder(context, NotesDatabase::class.java, "notes").build()
            val allNotes = db.notesDao().getAll()
            allNotes.collect {
                Log.d("all notes", it.toString())
            }
        }
    }

    fun addNote(context: Context, text: String) {
        viewModelScope.launch {
            val db = Room.databaseBuilder(context, NotesDatabase::class.java, "notes").build()
            db.notesDao().insert(CachedNote(0, text, false))
        }
    }
}