package com.example.memomind.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.memomind.data.local.dao.CardDao
import com.example.memomind.data.local.dao.DeckDao
import com.example.memomind.data.local.entity.CardEntity
import com.example.memomind.data.local.entity.DeckEntity
import com.example.memomind.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class, DeckEntity::class, CardEntity::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class MemoMindDatabase : RoomDatabase() {
    abstract fun deckDao(): DeckDao
    abstract fun cardDao(): CardDao
}