package com.example.memomind.data.local.dao

import androidx.room.*
import com.example.memomind.data.local.entity.DeckEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {
    @Query("SELECT * FROM decks WHERE syncStatus != 'PENDING_DELETE' ORDER BY updatedAt DESC")
    fun getAllDecks(): Flow<List<DeckEntity>>

    @Query("SELECT * FROM decks WHERE id = :id")
    suspend fun getDeckById(id: Long): DeckEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeck(deck: DeckEntity): Long

    @Update
    suspend fun updateDeck(deck: DeckEntity)

    @Query("DELETE FROM decks WHERE id = :id")
    suspend fun deleteDeck(id: Long)

    @Query("SELECT * FROM decks WHERE syncStatus = 'PENDING_UPLOAD'")
    suspend fun getPendingUploadDecks(): List<DeckEntity>

    @Query("SELECT * FROM decks WHERE syncStatus = 'PENDING_DELETE'")
    suspend fun getPendingDeleteDecks(): List<DeckEntity>

    @Query("UPDATE decks SET cardCount = (SELECT COUNT(*) FROM cards WHERE deckId = :deckId AND syncStatus != 'PENDING_DELETE') WHERE id = :deckId")
    suspend fun updateCardCount(deckId: Long)

    @Query("SELECT * FROM decks")
    suspend fun getAllDecksList(): List<DeckEntity>
}