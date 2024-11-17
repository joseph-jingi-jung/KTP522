package com.example.talevoice.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.Date

@Database(entities = [LocalTaleListItem::class], version = 1, exportSchema = false)
abstract class TaleDatabase : RoomDatabase() {
    abstract fun taleDao(): TaleDao
}