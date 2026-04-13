package com.example.memomind.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class SyncStatus { SYNCED, PENDING_UPLOAD, PENDING_DELETE }

@Entity(tableName = "decks")
data class DeckEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val serverId: String? = null,
    val name: String,
    val description: String = "",
    val cardCount: Int = 0,
    val syncStatus: SyncStatus = SyncStatus.PENDING_UPLOAD,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)