package com.example.memomind.data.repository

import com.example.memomind.data.local.dao.DeckDao
import com.example.memomind.data.local.entity.DeckEntity
import com.example.memomind.data.local.entity.SyncStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeckRepository @Inject constructor(
    private val deckDao: DeckDao,
) {
    fun getAllDecks(): Flow<List<DeckEntity>> = deckDao.getAllDecks()

    suspend fun getDeckById(id: Long): DeckEntity? = deckDao.getDeckById(id)

    suspend fun createDeck(name: String, description: String = ""): Long {
        val deck = DeckEntity(
            name = name,
            description = description,
            syncStatus = SyncStatus.PENDING_UPLOAD,
        )
        return deckDao.insertDeck(deck)
    }

    suspend fun updateDeck(deck: DeckEntity) {
        deckDao.updateDeck(
            deck.copy(
                syncStatus = SyncStatus.PENDING_UPLOAD,
                updatedAt = System.currentTimeMillis(),
            )
        )
    }

    suspend fun deleteDeck(deck: DeckEntity) {
        if (deck.serverId != null) {
            deckDao.updateDeck(deck.copy(syncStatus = SyncStatus.PENDING_DELETE))
        } else {
            deckDao.deleteDeck(deck.id)
        }
    }
}