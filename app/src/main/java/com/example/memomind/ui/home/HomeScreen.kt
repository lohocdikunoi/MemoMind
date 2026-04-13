package com.example.memomind.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.memomind.data.local.entity.DeckEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onDeckClick: (Long) -> Unit,
    onStatsClick: () -> Unit,
    onLogout: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val decks by viewModel.decks.collectAsState(initial = emptyList())

    LaunchedEffect(Unit) { viewModel.refreshReviewCount() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MemoMind") },
                actions = {
                    IconButton(onClick = onStatsClick) {
                        Icon(Icons.Default.BarChart, contentDescription = "Thống kê")
                    }
                    IconButton(onClick = { viewModel.logout(onLogout) }) {
                        Icon(Icons.Default.Logout, contentDescription = "Đăng xuất")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.showCreateDialog() }) {
                Icon(Icons.Default.Add, contentDescription = "Tạo bộ thẻ")
            }
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Xin chào, ${uiState.userName}!",
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (uiState.totalReviewCount > 0) {
                            "Bạn có ${uiState.totalReviewCount} thẻ cần ôn tập hôm nay"
                        } else {
                            "Tuyệt vời! Không có thẻ nào cần ôn tập"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }

            if (decks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.LibraryBooks,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Chưa có bộ thẻ nào",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            "Nhấn + để tạo bộ thẻ đầu tiên",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(decks, key = { it.id }) { deck ->
                        DeckItem(
                            deck = deck,
                            onClick = { onDeckClick(deck.id) },
                            onDelete = { viewModel.deleteDeck(deck) },
                        )
                    }
                }
            }
        }
    }

    if (uiState.showCreateDialog) {
        CreateDeckDialog(
            onDismiss = { viewModel.hideCreateDialog() },
            onCreate = { name, desc -> viewModel.createDeck(name, desc) },
        )
    }
}

@Composable
private fun DeckItem(
    deck: DeckEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = deck.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (deck.description.isNotBlank()) {
                    Text(
                        text = deck.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Text(
                    text = "${deck.cardCount} thẻ",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Xóa",
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Xóa bộ thẻ") },
            text = { Text("Bạn có chắc muốn xóa \"${deck.name}\"? Tất cả thẻ trong bộ sẽ bị xóa.") },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteDialog = false }) {
                    Text("Xóa", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Hủy") }
            },
        )
    }
}

@Composable
private fun CreateDeckDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tạo bộ thẻ mới") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên bộ thẻ") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Mô tả (tùy chọn)") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onCreate(name, description) },
                enabled = name.isNotBlank(),
            ) { Text("Tạo") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        },
    )
}