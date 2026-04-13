package com.example.memomind.ui.deck

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCardScreen(
    isEdit: Boolean = false,
    initialFront: String = "",
    initialBack: String = "",
    onSave: (String, String) -> Unit,
    onBack: () -> Unit,
) {
    var front by remember { mutableStateOf(initialFront) }
    var back by remember { mutableStateOf(initialBack) }

    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
            }
        }
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEdit) "Sửa thẻ" else "Thêm thẻ mới") },
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
        ) {
            OutlinedTextField(
                value = front,
                onValueChange = { front = it },
                label = { Text("Mặt trước (từ/câu hỏi)") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    if (front.isNotBlank()) {
                        IconButton(onClick = {
                            tts?.speak(front, TextToSpeech.QUEUE_FLUSH, null, null)
                        }) {
                            Icon(Icons.Default.VolumeUp, contentDescription = "Phát âm")
                        }
                    }
                },
                minLines = 3,
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = back,
                onValueChange = { back = it },
                label = { Text("Mặt sau (đáp án/nghĩa)") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    if (back.isNotBlank()) {
                        IconButton(onClick = {
                            tts?.speak(back, TextToSpeech.QUEUE_FLUSH, null, null)
                        }) {
                            Icon(Icons.Default.VolumeUp, contentDescription = "Phát âm")
                        }
                    }
                },
                minLines = 3,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (front.isNotBlank() && back.isNotBlank()) {
                        onSave(front, back)
                        onBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = front.isNotBlank() && back.isNotBlank(),
            ) {
                Text(if (isEdit) "Lưu thay đổi" else "Thêm thẻ")
            }
        }
    }
}