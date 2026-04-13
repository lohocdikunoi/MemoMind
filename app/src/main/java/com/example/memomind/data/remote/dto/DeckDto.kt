package com.example.memomind.data.remote.dto

data class DeckDto(
    val _id: String,
    val userId: String,
    val name: String,
    val description: String,
    val cardCount: Int,
    val createdAt: String,
    val updatedAt: String,
)

data class CreateDeckRequest(val name: String, val description: String = "")
data class UpdateDeckRequest(val name: String, val description: String)