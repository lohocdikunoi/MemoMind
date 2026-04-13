package com.example.memomind.ui.stats

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    onBack: () -> Unit,
    viewModel: StatsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thống kê") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Tổng quan học tập",
                style = MaterialTheme.typography.headlineMedium,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StatCard(
                    icon = Icons.Default.LibraryBooks,
                    label = "Bộ thẻ",
                    value = "${uiState.totalDecks}",
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    icon = Icons.Default.CreditCard,
                    label = "Tổng thẻ",
                    value = "${uiState.totalCards}",
                    modifier = Modifier.weight(1f),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StatCard(
                    icon = Icons.Default.CheckCircle,
                    label = "Đã học",
                    value = "${uiState.learnedCards}",
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    icon = Icons.Default.Schedule,
                    label = "Cần ôn hôm nay",
                    value = "${uiState.dueToday}",
                    modifier = Modifier.weight(1f),
                )
            }

            if (uiState.totalCards > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Tiến độ học tập", style = MaterialTheme.typography.titleMedium)
                LinearProgressIndicator(
                    progress = {
                        uiState.learnedCards.toFloat() / uiState.totalCards
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp),
                )
                Text(
                    text = "${(uiState.learnedCards * 100f / uiState.totalCards).toInt()}% hoàn thành",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}