package com.example.memomind.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memomind.data.local.entity.DeckEntity
import com.example.memomind.data.repository.AuthRepository
import com.example.memomind.data.repository.CardRepository
import com.example.memomind.data.repository.DeckRepository
import com.example.memomind.data.repository.SyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val userName: String = "",
    val totalReviewCount: Int = 0,
    val showCreateDialog: Boolean = false,
    val isSyncing: Boolean = false,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val deckRepository: DeckRepository,
    private val cardRepository: CardRepository,
    private val authRepository: AuthRepository,
    private val syncRepository: SyncRepository,
) : ViewModel() {

    val decks: Flow<List<DeckEntity>> = deckRepository.getAllDecks()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadUserInfo()
        syncFromServer()
    }

    private fun loadUserInfo() {
        viewModelScope.launch {
            val name = authRepository.getUserName() ?: "User"
            val reviewCount = cardRepository.getTotalReviewCount()
            _uiState.update { it.copy(userName = name, totalReviewCount = reviewCount) }
        }
    }

    private fun syncFromServer() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true) }
            syncRepository.pullFromServer()
            val reviewCount = cardRepository.getTotalReviewCount()
            _uiState.update { it.copy(isSyncing = false, totalReviewCount = reviewCount) }
        }
    }

    fun refreshReviewCount() {
        viewModelScope.launch {
            val count = cardRepository.getTotalReviewCount()
            _uiState.update { it.copy(totalReviewCount = count) }
        }
    }

    fun createDeck(name: String, description: String) {
        viewModelScope.launch {
            deckRepository.createDeck(name, description)
            _uiState.update { it.copy(showCreateDialog = false) }
        }
    }

    fun deleteDeck(deck: DeckEntity) {
        viewModelScope.launch {
            deckRepository.deleteDeck(deck)
        }
    }

    fun showCreateDialog() {
        _uiState.update { it.copy(showCreateDialog = true) }
    }

    fun hideCreateDialog() {
        _uiState.update { it.copy(showCreateDialog = false) }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onLoggedOut()
        }
    }
}