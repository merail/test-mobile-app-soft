package me.rail.mobileappsofttest.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppsDao {

    @Query("SELECT * FROM apps")
    fun getAll(): Flow<List<CachedNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg apps: CachedNote)

    @Update
    suspend fun update(vararg apps: CachedNote)

    @Delete
    suspend fun delete(vararg apps: CachedNote)

    @Query("UPDATE apps SET pin = :pin WHERE id =:id")
    suspend fun update(id: String, pin: Boolean)
}