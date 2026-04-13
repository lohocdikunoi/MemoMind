package com.example.memomind.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.example.memomind.data.local.dao.CardDao
import com.example.memomind.data.local.dao.DeckDao
import com.example.memomind.data.local.entity.SyncStatus
import com.example.memomind.data.remote.ApiService
import com.example.memomind.data.remote.dto.SyncCardItem
import com.example.memomind.data.remote.dto.SyncDeckItem
import com.example.memomind.data.remote.dto.SyncPushRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val api: ApiService,
    private val deckDao: DeckDao,
    private val cardDao: CardDao,
) : CoroutineWorker(context, workerParams) {

    companion object {
        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<SyncWorker>(
                30, TimeUnit.MINUTES,
            )
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    "sync_worker",
                    ExistingPeriodicWorkPolicy.KEEP,
                    request,
                )
        }
    }

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    override suspend fun doWork(): Result {
        return try {
            pushChanges()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private suspend fun pushChanges() {
        val pendingDecks = deckDao.getPendingUploadDecks()
        val pendingCards = cardDao.getPendingUploadCards()
        val deletedDecks = deckDao.getPendingDeleteDecks()
        val deletedCards = cardDao.getPendingDeleteCards()

        if (pendingDecks.isEmpty() && pendingCards.isEmpty() &&
            deletedDecks.isEmpty() && deletedCards.isEmpty()) return

        val request = SyncPushRequest(
            decks = pendingDecks.map { deck ->
                SyncDeckItem(
                    localId = deck.id,
                    serverId = deck.serverId,
                    name = deck.name,
                    description = deck.description,
                    cardCount = deck.cardCount,
                )
            },
            cards = pendingCards.map { card ->
                SyncCardItem(
                    localId = card.id,
                    serverId = card.serverId,
                    deckServerId = deckDao.getDeckById(card.deckId)?.serverId,
                    front = card.front,
                    back = card.back,
                    easeFactor = card.easeFactor,
                    interval = card.interval,
                    repetitions = card.repetitions,
                    nextReviewDate = dateFormat.format(Date(card.nextReviewDate)),
                    lastReviewDate = card.lastReviewDate?.let { dateFormat.format(Date(it)) },
                )
            },
            deletedDeckIds = deletedDecks.mapNotNull { it.serverId },
            deletedCardIds = deletedCards.mapNotNull { it.serverId },
        )

        val response = api.syncPush(request)
        if (response.isSuccessful) {
            val body = response.body() ?: return

            for (deckResult in body.decks) {
                if (deckResult.localId != null) {
                    val localDeck = deckDao.getDeckById(deckResult.localId) ?: continue
                    deckDao.updateDeck(localDeck.copy(
                        serverId = deckResult._id,
                        syncStatus = SyncStatus.SYNCED,
                    ))
                }
            }

            for (cardResult in body.cards) {
                if (cardResult.localId != null) {
                    val localCard = cardDao.getCardById(cardResult.localId) ?: continue
                    cardDao.updateCard(localCard.copy(
                        serverId = cardResult._id,
                        syncStatus = SyncStatus.SYNCED,
                    ))
                }
            }

            for (deck in pendingDecks) {
                deckDao.updateDeck(deck.copy(syncStatus = SyncStatus.SYNCED))
            }
            for (card in pendingCards) {
                cardDao.updateCard(card.copy(syncStatus = SyncStatus.SYNCED))
            }

            for (deck in deletedDecks) {
                deckDao.deleteDeck(deck.id)
            }
            for (card in deletedCards) {
                cardDao.deleteCard(card.id)
            }
        }
    }
}