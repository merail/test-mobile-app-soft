package me.rail.mobileappsofttest.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    @Query("SELECT * FROM notes")
    fun getAll(): Flow<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg note: Note)

    @Update
    suspend fun update(vararg note: Note)

    @Delete
    suspend fun delete(vararg note: Note)

    @Query("UPDATE notes SET position = :position + 1 WHERE position = :position")
    suspend fun incrementPosition(position: Int)

    @Query("UPDATE notes SET positionBeforePin = :position + 1 WHERE positionBeforePin = :position")
    suspend fun incrementPositionBeforePin(position: Int)

    @Query("UPDATE notes SET position = :position - 1 WHERE position = :position")
    suspend fun decrementPosition(position: Int)

    @Query("UPDATE notes SET positionBeforePin = :position - 1 WHERE positionBeforePin = :position")
    suspend fun decrementPositionBeforePin(position: Int)

    @Query("SELECT COUNT(position) FROM notes")
    fun getCount(): Int

    @Query("SELECT COUNT(position) FROM notes WHERE pin = 1")
    fun getPinnedCount(): Int
}