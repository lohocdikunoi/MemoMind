package com.example.memomind.data.remote

import com.example.memomind.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/refresh")
    suspend fun refresh(@Body request: RefreshRequest): Response<TokenResponse>

    @GET("api/decks")
    suspend fun getDecks(): Response<List<DeckDto>>

    @POST("api/decks")
    suspend fun createDeck(@Body request: CreateDeckRequest): Response<DeckDto>

    @PUT("api/decks/{id}")
    suspend fun updateDeck(@Path("id") id: String, @Body request: UpdateDeckRequest): Response<DeckDto>

    @DELETE("api/decks/{id}")
    suspend fun deleteDeck(@Path("id") id: String): Response<Unit>

    @GET("api/decks/{deckId}/cards")
    suspend fun getCards(@Path("deckId") deckId: String): Response<List<CardDto>>

    @POST("api/decks/{deckId}/cards")
    suspend fun createCard(@Path("deckId") deckId: String, @Body request: CreateCardRequest): Response<CardDto>

    @PUT("api/cards/{id}")
    suspend fun updateCard(@Path("id") id: String, @Body request: UpdateCardRequest): Response<CardDto>

    @DELETE("api/cards/{id}")
    suspend fun deleteCard(@Path("id") id: String): Response<Unit>

    @GET("api/decks/{deckId}/review")
    suspend fun getReviewCards(@Path("deckId") deckId: String): Response<List<CardDto>>

    @POST("api/cards/{id}/review")
    suspend fun submitReview(@Path("id") id: String, @Body request: ReviewRequest): Response<CardDto>

    @POST("api/sync/push")
    suspend fun syncPush(@Body request: SyncPushRequest): Response<SyncPushResponse>

    @GET("api/sync/pull")
    suspend fun syncPull(@Query("since") since: String): Response<SyncPullResponse>
}