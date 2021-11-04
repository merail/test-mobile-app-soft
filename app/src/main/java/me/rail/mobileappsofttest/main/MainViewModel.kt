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

    private val mutableAddingNoteFromSelectedNote = MutableLiveData<Unit>()
    val addingNoteFromSelectedNote: LiveData<Unit> get() = mutableAddingNoteFromSelectedNote

    val notes = liveData {
        val allNotes = notesDao.getAll()
        allNotes.collect {
            emit(it)
            Log.d("notes", it.toString())
        }
    }

    val pinnedCount = liveData {
        val count = notesDao.getPinnedCount()
        count.collect {
            emit(it)
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

            val pinnedCount = pinnedCount.value ?: 0

            val position = if (isFromPinClick || note.pin) 0 else pinnedCount

            for (i in note.position downTo position) {
                notesDao.incrementPosition(i)
                notesDao.incrementPositionBeforePin(i)
            }

            val positionBeforePin =
                if (isFromPinClick) note.positionBeforePin else pinnedCount
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

    fun setIsAddingNoteFromSelectedNote() {
        mutableAddingNoteFromSelectedNote.value = Unit
    }

    fun onNoteMove(from: Int, to: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            notes.value?.let { notes ->
                val noteFrom = notes[from]

                val pinnedCount = pinnedCount.value ?: 0

                val to1 = if (noteFrom.pin) {
                    if (to >= pinnedCount) pinnedCount - 1 else to
                } else {
                    if (to >= pinnedCount) to else pinnedCount
                }

                val noteTo = notes[to1]

                notesDao.delete(noteFrom)
                notesDao.delete(noteTo)

                notesDao.insert(
                    noteFrom.copy(
                        position = noteTo.position,
                        positionBeforePin = noteTo.position
                    )
                )
                if (from > to1) {
                    for (i in from - 1 downTo to1 + 1) {
                        notesDao.incrementPosition(i)
                        notesDao.incrementPositionBeforePin(i)
                    }

                    notesDao.insert(
                        noteTo.copy(
                            position = noteTo.position + 1,
                            positionBeforePin = noteTo.position + 1
                        )
                    )
                } else {
                    for (i in from + 1 until to1) {
                        notesDao.decrementPosition(i)
                        notesDao.decrementPositionBeforePin(i)
                    }

                    notesDao.insert(
                        noteTo.copy(
                            position = noteTo.position - 1,
                            positionBeforePin = noteTo.position - 1
                        )
                    )
                }
            }
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            notesDao.update(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            notesDao.delete(note)

            for (i in note.position + 1..notesDao.getCount()) {
                Log.d("test2", i.toString())
                notesDao.decrementPosition(i)
                notesDao.decrementPositionBeforePin(i)
            }
        }
    }
}