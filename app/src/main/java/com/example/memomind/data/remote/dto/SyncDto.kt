package com.example.memomind.data.remote.dto

data class SyncPushRequest(
    val decks: List<SyncDeckItem> = emptyList(),
    val cards: List<SyncCardItem> = emptyList(),
    val deletedDeckIds: List<String> = emptyList(),
    val deletedCardIds: List<String> = emptyList(),
)

data class SyncDeckItem(
    val localId: Long? = null,
    val serverId: String? = null,
    val name: String,
    val description: String,
    val cardCount: Int,
)

data class SyncCardItem(
    val localId: Long? = null,
    val serverId: String? = null,
    val deckServerId: String? = null,
    val front: String,
    val back: String,
    val easeFactor: Double,
    val interval: Int,
    val repetitions: Int,
    val nextReviewDate: String,
    val lastReviewDate: String?,
)

data class SyncPushResponse(
    val decks: List<SyncDeckResult>,
    val cards: List<SyncCardResult>,
)

data class SyncDeckResult(
    val _id: String,
    val name: String,
    val localId: Long? = null,
)

data class SyncCardResult(
    val _id: String,
    val front: String,
    val localId: Long? = null,
)

data class SyncPullResponse(
    val decks: List<DeckDto>,
    val cards: List<CardDto>,
    val serverTime: String,
)