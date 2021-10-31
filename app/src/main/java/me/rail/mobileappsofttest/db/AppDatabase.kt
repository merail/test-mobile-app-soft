package me.rail.mobileappsofttest.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CachedNote::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appsDao(): AppsDao
}