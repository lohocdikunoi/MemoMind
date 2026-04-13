package com.example.memomind.data.repository

import com.example.memomind.data.local.dao.CardDao
import com.example.memomind.data.local.dao.DeckDao
import com.example.memomind.data.local.entity.CardEntity
import com.example.memomind.data.local.entity.DeckEntity
import com.example.memomind.data.local.entity.SyncStatus
import com.example.memomind.data.remote.ApiService
import com.example.memomind.data.remote.dto.CardDto
import com.example.memomind.data.remote.dto.DeckDto
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepository @Inject constructor(
    private val api: ApiService,
    private val deckDao: DeckDao,
    private val cardDao: CardDao,
) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private fun parseDate(dateStr: String?): Long {
        if (dateStr == null) return System.currentTimeMillis()
        return try {
            dateFormat.parse(dateStr)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }

    suspend fun pullFromServer(): Result<Unit> {
        return try {
            // Pull tất cả decks từ server
            val decksResponse = api.getDecks()
            if (!decksResponse.isSuccessful) {
                return Result.failure(Exception("Không thể lấy dữ liệu decks"))
            }
            val serverDecks = decksResponse.body() ?: emptyList()

            // Lưu decks vào Room
            for (dto in serverDecks) {
                val existing = findDeckByServerId(dto._id)
                if (existing != null) {
                    deckDao.updateDeck(existing.copy(
                        name = dto.name,
                        description = dto.description,
                        cardCount = dto.cardCount,
                        syncStatus = SyncStatus.SYNCED,
                        updatedAt = parseDate(dto.updatedAt),
                    ))
                } else {
                    deckDao.insertDeck(DeckEntity(
                        serverId = dto._id,
                        name = dto.name,
                        description = dto.description,
                        cardCount = dto.cardCount,
                        syncStatus = SyncStatus.SYNCED,
                        createdAt = parseDate(dto.createdAt),
                        updatedAt = parseDate(dto.updatedAt),
                    ))
                }
            }

            // Pull cards cho từng deck
            for (dto in serverDecks) {
                val localDeck = findDeckByServerId(dto._id) ?: continue
                val cardsResponse = api.getCards(dto._id)
                if (!cardsResponse.isSuccessful) continue
                val serverCards = cardsResponse.body() ?: emptyList()

                for (cardDto in serverCards) {
                    val existingCard = findCardByServerId(cardDto._id)
                    if (existingCard != null) {
                        cardDao.updateCard(existingCard.copy(
                            front = cardDto.front,
                            back = cardDto.back,
                            easeFactor = cardDto.easeFactor,
                            interval = cardDto.interval,
                            repetitions = cardDto.repetitions,
                            nextReviewDate = parseDate(cardDto.nextReviewDate),
                            lastReviewDate = cardDto.lastReviewDate?.let { parseDate(it) },
                            syncStatus = SyncStatus.SYNCED,
                            updatedAt = parseDate(cardDto.updatedAt),
                        ))
                    } else {
                        cardDao.insertCard(CardEntity(
                            serverId = cardDto._id,
                            deckId = localDeck.id,
                            front = cardDto.front,
                            back = cardDto.back,
                            easeFactor = cardDto.easeFactor,
                            interval = cardDto.interval,
                            repetitions = cardDto.repetitions,
                            nextReviewDate = parseDate(cardDto.nextReviewDate),
                            lastReviewDate = cardDto.lastReviewDate?.let { parseDate(it) },
                            syncStatus = SyncStatus.SYNCED,
                            createdAt = parseDate(cardDto.createdAt),
                            updatedAt = parseDate(cardDto.updatedAt),
                        ))
                    }
                }

                // Cập nhật cardCount
                deckDao.updateCardCount(localDeck.id)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun findDeckByServerId(serverId: String): DeckEntity? {
        return deckDao.getAllDecksList().find { it.serverId == serverId }
    }

    private suspend fun findCardByServerId(serverId: String): CardEntity? {
        return cardDao.getAllCardsList().find { it.serverId == serverId }
    }
}