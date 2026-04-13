package com.example.memomind.ui.navigation

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.memomind.ui.auth.AuthScreen
import com.example.memomind.ui.deck.AddEditCardScreen
import com.example.memomind.ui.deck.DeckDetailScreen
import com.example.memomind.ui.deck.DeckViewModel
import com.example.memomind.ui.home.HomeScreen
import com.example.memomind.ui.review.ReviewScreen
import com.example.memomind.ui.stats.StatsScreen

sealed class Screen(val route: String) {
    data object Auth : Screen("auth")
    data object Home : Screen("home")
    data object DeckDetail : Screen("deck/{deckId}") {
        fun createRoute(deckId: Long) = "deck/$deckId"
    }
    data object AddCard : Screen("deck/{deckId}/add") {
        fun createRoute(deckId: Long) = "deck/$deckId/add"
    }
    data object EditCard : Screen("deck/{deckId}/edit/{cardId}") {
        fun createRoute(deckId: Long, cardId: Long) = "deck/$deckId/edit/$cardId"
    }
    data object Review : Screen("review/{deckId}") {
        fun createRoute(deckId: Long) = "review/$deckId"
    }
    data object Stats : Screen("stats")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(Screen.Auth.route) {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                },
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onDeckClick = { deckId ->
                    navController.navigate(Screen.DeckDetail.createRoute(deckId))
                },
                onStatsClick = { navController.navigate(Screen.Stats.route) },
                onLogout = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
            )
        }

        composable(
            route = Screen.DeckDetail.route,
            arguments = listOf(navArgument("deckId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getLong("deckId") ?: 0L
            DeckDetailScreen(
                onBack = { navController.popBackStack() },
                onStartReview = { navController.navigate(Screen.Review.createRoute(it)) },
                onAddCard = { navController.navigate(Screen.AddCard.createRoute(deckId)) },
                onEditCard = { cardId ->
                    navController.navigate(Screen.EditCard.createRoute(deckId, cardId))
                },
            )
        }

        composable(
            route = Screen.AddCard.route,
            arguments = listOf(navArgument("deckId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getLong("deckId") ?: 0L
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.DeckDetail.createRoute(deckId))
            }
            val deckViewModel: DeckViewModel = hiltViewModel(parentEntry)

            AddEditCardScreen(
                onSave = { front, back -> deckViewModel.addCard(front, back) },
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Screen.EditCard.route,
            arguments = listOf(
                navArgument("deckId") { type = NavType.LongType },
                navArgument("cardId") { type = NavType.LongType },
            ),
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getLong("deckId") ?: 0L
            val cardId = backStackEntry.arguments?.getLong("cardId") ?: 0L
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.DeckDetail.createRoute(deckId))
            }
            val deckViewModel: DeckViewModel = hiltViewModel(parentEntry)
            val cards by deckViewModel.cards.collectAsState(initial = emptyList())
            val card = cards.find { it.id == cardId }

            if (card != null) {
                AddEditCardScreen(
                    isEdit = true,
                    initialFront = card.front,
                    initialBack = card.back,
                    onSave = { front, back -> deckViewModel.updateCard(card, front, back) },
                    onBack = { navController.popBackStack() },
                )
            }
        }

        composable(
            route = Screen.Review.route,
            arguments = listOf(navArgument("deckId") { type = NavType.LongType }),
        ) {
            ReviewScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Stats.route) {
            StatsScreen(onBack = { navController.popBackStack() })
        }
    }
}