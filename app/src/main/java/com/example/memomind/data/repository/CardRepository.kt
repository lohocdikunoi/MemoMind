package com.example.memomind.data.repository

import com.example.memomind.data.local.dao.CardDao
import com.example.memomind.data.local.dao.DeckDao
import com.example.memomind.data.local.entity.CardEntity
import com.example.memomind.data.local.entity.SyncStatus
import com.example.memomind.util.SM2Algorithm
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardRepository @Inject constructor(
    private val cardDao: CardDao,
    private val deckDao: DeckDao,
) {
    fun getCardsByDeck(deckId: Long): Flow<List<CardEntity>> = cardDao.getCardsByDeck(deckId)

    suspend fun getCardById(id: Long): CardEntity? = cardDao.getCardById(id)

    suspend fun createCard(deckId: Long, front: String, back: String): Long {
        val card = CardEntity(
            deckId = deckId,
            front = front,
            back = back,
            syncStatus = SyncStatus.PENDING_UPLOAD,
        )
        val id = cardDao.insertCard(card)
        deckDao.updateCardCount(deckId)
        return id
    }

    suspend fun updateCard(card: CardEntity) {
        cardDao.updateCard(
            card.copy(
                syncStatus = SyncStatus.PENDING_UPLOAD,
                updatedAt = System.currentTimeMillis(),
            )
        )
    }

    suspend fun deleteCard(card: CardEntity) {
        if (card.serverId != null) {
            cardDao.updateCard(card.copy(syncStatus = SyncStatus.PENDING_DELETE))
        } else {
            cardDao.deleteCard(card.id)
        }
        deckDao.updateCardCount(card.deckId)
    }

    suspend fun getReviewCards(deckId: Long): List<CardEntity> {
        return cardDao.getReviewCards(deckId, System.currentTimeMillis())
    }

    suspend fun submitReview(card: CardEntity, quality: Int): CardEntity {
        val updated = SM2Algorithm.applyToCard(card, quality).copy(
            syncStatus = SyncStatus.PENDING_UPLOAD,
        )
        cardDao.updateCard(updated)
        return updated
    }

    suspend fun getTotalReviewCount(): Int = cardDao.getTotalReviewCount(System.currentTimeMillis())

    suspend fun getLearnedCardCount(): Int = cardDao.getLearnedCardCount()
}