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

    @Query("UPDATE notes SET pin = :pin WHERE id =:id")
    suspend fun update(id: String, pin: Boolean)
}