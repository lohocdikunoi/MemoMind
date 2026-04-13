package com.example.memomind.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memomind.data.repository.CardRepository
import com.example.memomind.data.repository.DeckRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StatsUiState(
    val totalDecks: Int = 0,
    val totalCards: Int = 0,
    val learnedCards: Int = 0,
    val dueToday: Int = 0,
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val deckRepository: DeckRepository,
    private val cardRepository: CardRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            deckRepository.getAllDecks().collect { decks ->
                val totalCards = decks.sumOf { it.cardCount }
                val learnedCards = cardRepository.getLearnedCardCount()
                val dueToday = cardRepository.getTotalReviewCount()
                _uiState.update {
                    StatsUiState(
                        totalDecks = decks.size,
                        totalCards = totalCards,
                        learnedCards = learnedCards,
                        dueToday = dueToday,
                    )
                }
            }
        }
    }
}

