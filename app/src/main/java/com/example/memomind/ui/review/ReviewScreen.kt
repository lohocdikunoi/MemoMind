package com.example.memomind.ui.review

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    onBack: () -> Unit,
    viewModel: ReviewViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Ôn tập ${uiState.reviewedCount}/${uiState.totalCards}")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
            )
        },
    ) { padding ->
        if (uiState.isCompleted) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Hoàn thành!",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Bạn đã ôn tập ${uiState.totalCards} thẻ",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onBack) {
                    Text("Quay lại")
                }
            }
        } else {
            val currentCard = uiState.cards.getOrNull(uiState.currentIndex)
            if (currentCard != null) {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    LinearProgressIndicator(
                        progress = {
                            if (uiState.totalCards > 0) {
                                uiState.reviewedCount.toFloat() / uiState.totalCards
                            } else 0f
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    val rotation by animateFloatAsState(
                        targetValue = if (uiState.isFlipped) 180f else 0f,
                        animationSpec = tween(400),
                        label = "flip",
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .graphicsLayer {
                                rotationY = rotation
                                cameraDistance = 12f * density
                            }
                            .clickable { viewModel.flipCard() },
                        colors = CardDefaults.cardColors(
                            containerColor = if (rotation <= 90f) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.tertiaryContainer
                            },
                        ),
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (rotation <= 90f) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = currentCard.front,
                                        style = MaterialTheme.typography.headlineMedium,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(24.dp),
                                    )
                                    IconButton(onClick = { viewModel.speak(currentCard.front) }) {
                                        Icon(Icons.Default.VolumeUp, contentDescription = "Phát âm")
                                    }
                                }
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.graphicsLayer { rotationY = 180f },
                                ) {
                                    Text(
                                        text = currentCard.back,
                                        style = MaterialTheme.typography.headlineMedium,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(24.dp),
                                    )
                                    IconButton(onClick = { viewModel.speak(currentCard.back) }) {
                                        Icon(Icons.Default.VolumeUp, contentDescription = "Phát âm")
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (!uiState.isFlipped) "Nhấn thẻ để lật" else "Chọn mức độ nhớ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (uiState.isFlipped) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                QualityButton(
                                    label = "Không nhớ",
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.weight(1f),
                                    onClick = { viewModel.submitReview(0) },
                                )
                                QualityButton(
                                    label = "Mơ hồ",
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.weight(1f),
                                    onClick = { viewModel.submitReview(1) },
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                QualityButton(
                                    label = "Khó",
                                    color = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.weight(1f),
                                    onClick = { viewModel.submitReview(3) },
                                )
                                QualityButton(
                                    label = "Tốt",
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.weight(1f),
                                    onClick = { viewModel.submitReview(4) },
                                )
                                QualityButton(
                                    label = "Dễ",
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.weight(1f),
                                    onClick = { viewModel.submitReview(5) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QualityButton(
    label: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = color),
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge)
    }
}