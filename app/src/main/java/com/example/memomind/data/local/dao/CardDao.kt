package com.example.memomind.data.local.dao

import androidx.room.*
import com.example.memomind.data.local.entity.CardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
    @Query("SELECT * FROM cards WHERE deckId = :deckId AND syncStatus != 'PENDING_DELETE' ORDER BY createdAt DESC")
    fun getCardsByDeck(deckId: Long): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE id = :id")
    suspend fun getCardById(id: Long): CardEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: CardEntity): Long

    @Update
    suspend fun updateCard(card: CardEntity)

    @Query("DELETE FROM cards WHERE id = :id")
    suspend fun deleteCard(id: Long)

    @Query("SELECT * FROM cards WHERE deckId = :deckId AND nextReviewDate <= :now AND syncStatus != 'PENDING_DELETE' ORDER BY nextReviewDate ASC")
    suspend fun getReviewCards(deckId: Long, now: Long): List<CardEntity>

    @Query("SELECT COUNT(*) FROM cards WHERE nextReviewDate <= :now AND syncStatus != 'PENDING_DELETE'")
    suspend fun getTotalReviewCount(now: Long): Int

    @Query("SELECT * FROM cards WHERE syncStatus = 'PENDING_UPLOAD'")
    suspend fun getPendingUploadCards(): List<CardEntity>

    @Query("SELECT * FROM cards WHERE syncStatus = 'PENDING_DELETE'")
    suspend fun getPendingDeleteCards(): List<CardEntity>

    @Query("SELECT COUNT(*) FROM cards WHERE deckId = :deckId AND syncStatus != 'PENDING_DELETE'")
    suspend fun getCardCountByDeck(deckId: Long): Int

    @Query("SELECT COUNT(*) FROM cards WHERE lastReviewDate IS NOT NULL AND syncStatus != 'PENDING_DELETE'")
    suspend fun getLearnedCardCount(): Int

    @Query("SELECT * FROM cards")
    suspend fun getAllCardsList(): List<CardEntity>
}