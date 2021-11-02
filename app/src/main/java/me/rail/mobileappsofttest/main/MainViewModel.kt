package me.rail.mobileappsofttest.main

import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.rail.mobileappsofttest.db.Note
import me.rail.mobileappsofttest.db.NotesDao
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val notesDao: NotesDao) : ViewModel() {
    private val mutableTextForShare = MutableLiveData<String>()
    val textForShare: LiveData<String> get() = mutableTextForShare

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

            val lastPositionInCycle = if (isFromPinClick) 0 else notesDao.getPinnedCount()

            for (i in note.position downTo lastPositionInCycle) {
                notesDao.incrementPosition(i)
                notesDao.incrementPositionBeforePin(i)
            }

            val position = if (isFromPinClick) 0 else notesDao.getPinnedCount()
            val positionBeforePin =
                if (isFromPinClick) note.positionBeforePin else notesDao.getPinnedCount()
            val pin = if (isFromPinClick) true else note.pin
            notesDao.insert(
                note.copy(
                    position = position,
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
                for (i in note.position..note.positionBeforePin) {
                    notesDao.decrementPosition(i)
                    notesDao.decrementPositionBeforePin(i)
                }

                notesDao.insert(note.copy(position = note.positionBeforePin, pin = false))
            }
        }
    }

    fun selectTextForShare(text: String) {
        mutableTextForShare.value = text
    }
}