package com.example.memomind.data.local

import androidx.room.TypeConverter
import com.example.memomind.data.local.entity.SyncStatus

class Converters {
    @TypeConverter
    fun fromSyncStatus(value: SyncStatus): String = value.name

    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus = SyncStatus.valueOf(value)
}