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
            notesDao.insert(Note(notesDao.getCount(), text, false, notesDao.getCount()))
        }
    }

    fun setNoteToTop(note: Note, isFromPinClick: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            notesDao.delete(note)
            for (i in note.position downTo notesDao.getPinnedCount()) {
                notesDao.incrementPosition(i)
                notesDao.incrementPositionBeforePin(i)
            }

            val positionBeforePin = if (isFromPinClick) note.positionBeforePin else notesDao.getPinnedCount()
            val pin = if (isFromPinClick) true else note.pin
            notesDao.insert(
                note.copy(
                    position = notesDao.getPinnedCount(),
                    pin = pin,
                    positionBeforePin = positionBeforePin
                )
            )
        }
    }

    fun togglePin(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            if (!note.pin)
                setNoteToTop(note, true)
            else {
                notesDao.delete(note)
                for (i in 1..note.positionBeforePin) {
                    notesDao.decrementPosition(i)
                    notesDao.decrementPositionBeforePin(i)
                }

                notesDao.insert(note.copy(position = note.positionBeforePin, pin = false))
            }
        }
    }
}