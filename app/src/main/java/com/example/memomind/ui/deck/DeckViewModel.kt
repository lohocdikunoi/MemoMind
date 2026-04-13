package com.example.memomind.ui.deck

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memomind.data.local.entity.CardEntity
import com.example.memomind.data.local.entity.DeckEntity
import com.example.memomind.data.repository.CardRepository
import com.example.memomind.data.repository.DeckRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DeckDetailUiState(
    val deck: DeckEntity? = null,
    val reviewCount: Int = 0,
)

@HiltViewModel
class DeckViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val deckRepository: DeckRepository,
    private val cardRepository: CardRepository,
) : ViewModel() {

    private val deckId: Long = savedStateHandle.get<Long>("deckId") ?: 0L

    val cards: Flow<List<CardEntity>> = cardRepository.getCardsByDeck(deckId)

    private val _uiState = MutableStateFlow(DeckDetailUiState())
    val uiState: StateFlow<DeckDetailUiState> = _uiState.asStateFlow()

    init {
        loadDeck()
    }

    private fun loadDeck() {
        viewModelScope.launch {
            val deck = deckRepository.getDeckById(deckId)
            val reviewCount = cardRepository.getReviewCards(deckId).size
            _uiState.update { it.copy(deck = deck, reviewCount = reviewCount) }
        }
    }

    fun addCard(front: String, back: String) {
        viewModelScope.launch {
            cardRepository.createCard(deckId, front, back)
            loadDeck()
        }
    }

    fun updateCard(card: CardEntity, front: String, back: String) {
        viewModelScope.launch {
            cardRepository.updateCard(card.copy(front = front, back = back))
        }
    }

    fun deleteCard(card: CardEntity) {
        viewModelScope.launch {
            cardRepository.deleteCard(card)
            loadDeck()
        }
    }

    fun updateDeck(name: String, description: String) {
        viewModelScope.launch {
            val deck = _uiState.value.deck ?: return@launch
            deckRepository.updateDeck(deck.copy(name = name, description = description))
            loadDeck()
        }
    }

    fun refreshReviewCount() {
        viewModelScope.launch {
            val count = cardRepository.getReviewCards(deckId).size
            _uiState.update { it.copy(reviewCount = count) }
        }
    }
}