package me.rail.mobileappsofttest.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    @Query("SELECT * FROM notes")
    fun getAll(): Flow<List<CachedNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg note: CachedNote)

    @Update
    suspend fun update(vararg note: CachedNote)

    @Delete
    suspend fun delete(vararg note: CachedNote)

    @Query("UPDATE notes SET pin = :pin WHERE id =:id")
    suspend fun update(id: String, pin: Boolean)
}