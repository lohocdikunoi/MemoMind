package com.example.memomind.ui.review

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memomind.data.local.entity.CardEntity
import com.example.memomind.data.repository.CardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

data class ReviewUiState(
    val cards: List<CardEntity> = emptyList(),
    val currentIndex: Int = 0,
    val isFlipped: Boolean = false,
    val isCompleted: Boolean = false,
    val totalCards: Int = 0,
    val reviewedCount: Int = 0,
)

@HiltViewModel
class ReviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val cardRepository: CardRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val deckId: Long = savedStateHandle.get<Long>("deckId") ?: 0L

    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    private var tts: TextToSpeech? = null

    init {
        loadReviewCards()
        initTts()
    }

    private fun initTts() {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
            }
        }
    }

    private fun loadReviewCards() {
        viewModelScope.launch {
            val cards = cardRepository.getReviewCards(deckId)
            _uiState.update {
                it.copy(
                    cards = cards,
                    totalCards = cards.size,
                    currentIndex = 0,
                    isFlipped = false,
                    isCompleted = cards.isEmpty(),
                )
            }
        }
    }

    fun flipCard() {
        _uiState.update { it.copy(isFlipped = !it.isFlipped) }
    }

    fun submitReview(quality: Int) {
        viewModelScope.launch {
            val state = _uiState.value
            val currentCard = state.cards.getOrNull(state.currentIndex) ?: return@launch

            cardRepository.submitReview(currentCard, quality)

            val nextIndex = state.currentIndex + 1
            if (nextIndex >= state.cards.size) {
                _uiState.update {
                    it.copy(isCompleted = true, reviewedCount = state.reviewedCount + 1)
                }
            } else {
                _uiState.update {
                    it.copy(
                        currentIndex = nextIndex,
                        isFlipped = false,
                        reviewedCount = state.reviewedCount + 1,
                    )
                }
            }
        }
    }

    fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onCleared() {
        tts?.stop()
        tts?.shutdown()
        super.onCleared()
    }
}