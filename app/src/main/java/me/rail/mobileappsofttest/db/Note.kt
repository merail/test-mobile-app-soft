package me.rail.mobileappsofttest.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "pin") val pin: Boolean
)