package com.example.talevoice.data.source.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.talevoice.data.TaleListItem

@Dao
interface TaleDao {

    @Transaction
    @Query("SELECT * FROM Tale")
    suspend fun getTaleList(): List<TaleListItem>
}