package com.example.memomind.data.remote.dto

data class CardDto(
    val _id: String,
    val deckId: String,
    val userId: String,
    val front: String,
    val back: String,
    val easeFactor: Double,
    val interval: Int,
    val repetitions: Int,
    val nextReviewDate: String,
    val lastReviewDate: String?,
    val createdAt: String,
    val updatedAt: String,
)

data class CreateCardRequest(val front: String, val back: String)
data class UpdateCardRequest(val front: String, val back: String)
data class ReviewRequest(val quality: Int)