package com.eduhds.jogodamemoria.ui.screens

import android.app.Activity
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.eduhds.jogodamemoria.R
import com.eduhds.jogodamemoria.model.GameCollection
import com.eduhds.jogodamemoria.model.MemoryCard
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(game: GameCollection?, onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    // Trava a orientação em portrait ao entrar na tela e restaura ao sair
    DisposableEffect(context) {
        var currentContext = context
        while (currentContext is ContextWrapper) {
            if (currentContext is Activity) break
            currentContext = currentContext.baseContext
        }
        val activity = currentContext as? Activity
        val originalOrientation =
            activity?.requestedOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        onDispose {
            activity?.requestedOrientation = originalOrientation
        }
    }

    val successPlayer = remember { MediaPlayer.create(context, R.raw.success) }
    val errorPlayer = remember { MediaPlayer.create(context, R.raw.error) }

    val totalCards = 24

    val gameCards = remember(game) {
        val subset = game?.cards?.shuffled()?.take(totalCards / 2) ?: emptyList()
        (subset + subset).shuffled().map { card ->
            card.copy(isFlipped = false, isMatched = false)
        }.toMutableStateList()
    }

    // Rastreia os índices dos cards que foram virados nesta rodada
    val flippedIndices = remember(game) { mutableStateListOf<Int>() }
    // Rastreia os índices dos cards para feedback visual temporário (verde/vermelho)
    val successIndices = remember(game) { mutableStateListOf<Int>() }
    val errorIndices = remember(game) { mutableStateListOf<Int>() }

    // Score
    var successCount by remember { mutableIntStateOf(0) }
    var errorCount by remember { mutableIntStateOf(0) }

    // Configurações de efeitos (som e vibração)
    var effectsEnabled by remember { mutableStateOf(true) }

    DisposableEffect(Unit) {
        onDispose {
            successPlayer?.release()
            errorPlayer?.release()
        }
    }

    // Lógica para lidar com o par de cards virados
    LaunchedEffect(flippedIndices.size) {
        if (flippedIndices.size == 2) {
            val index1 = flippedIndices[0]
            val index2 = flippedIndices[1]

            if (gameCards[index1].id == gameCards[index2].id) {
                // Houve match: marca ambos como matched e mostra feedback verde
                successCount++
                if (effectsEnabled) {
                    successPlayer?.start()
                }
                successIndices.add(index1)
                successIndices.add(index2)
                gameCards[index1] = gameCards[index1].copy(isMatched = true, isFlipped = true)
                gameCards[index2] = gameCards[index2].copy(isMatched = true, isFlipped = true)
                delay(1000)
                successIndices.remove(index1)
                successIndices.remove(index2)
            } else {
                // Não houve match: mostra feedback vermelho e desvira os cards após 1s
                errorCount++
                if (effectsEnabled) {
                    errorPlayer?.start()
                }
                errorIndices.add(index1)
                errorIndices.add(index2)
                delay(1000)
                gameCards[index1] = gameCards[index1].copy(isFlipped = false)
                gameCards[index2] = gameCards[index2].copy(isFlipped = false)
                errorIndices.remove(index1)
                errorIndices.remove(index2)
            }
            flippedIndices.clear()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Jogo da Memória") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Card(
                    modifier = Modifier
                        .width(28.dp)
                        .height(28.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        AsyncImage(
                            model = game?.icon,
                            contentDescription = "Game Icon",
                            modifier = Modifier
                                .height(24.dp)
                                .width(24.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                Text(
                    text = game?.title ?: "",
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(text = "Acertos", fontSize = 12.sp)
                    Text(
                        text = successCount.toString(),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 24.sp
                    )
                }
                VerticalDivider(modifier = Modifier.height(48.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(text = "Erros", fontSize = 12.sp)
                    Text(
                        text = errorCount.toString(),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 24.sp
                    )
                }
                VerticalDivider(modifier = Modifier.height(48.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Switch(
                        checked = effectsEnabled,
                        onCheckedChange = { effectsEnabled = it },
                        modifier = Modifier.scale(0.7f)
                    )
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Efeitos",
                        modifier = Modifier.size(20.dp),
                        tint = if (effectsEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider()

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                userScrollEnabled = false,
                verticalArrangement = Arrangement.Center,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                itemsIndexed(gameCards) { index, card ->
                    MemoryCardItem(
                        card = card,
                        isSuccess = successIndices.contains(index),
                        isError = errorIndices.contains(index),
                        onClick = {
                            // Só vira se o card estiver virado para baixo,
                            // não for um match e não tivermos 2 cards já virados processando
                            if (flippedIndices.size < 2 && !card.isFlipped && !card.isMatched) {
                                if (effectsEnabled) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                                gameCards[index] = card.copy(isFlipped = true)
                                flippedIndices.add(index)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MemoryCardItem(
    card: MemoryCard,
    isSuccess: Boolean,
    isError: Boolean,
    onClick: () -> Unit
) {
    // Animação de rotação Y
    val rotation by animateFloatAsState(
        targetValue = if (card.isFlipped || card.isMatched) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "cardFlipRotation"
    )

    Card(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .aspectRatio(1f)
            .fillMaxWidth()
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12 * density
            },
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (rotation <= 90f) {
                MaterialTheme.colorScheme.primary
            } else {
                when {
                    isSuccess -> Color(0xFF4CAF50) // Verde
                    isError -> Color(0xFFF44336) // Vermelho
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
            }
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (rotation <= 90f) {
                // Lado de trás (Conteúdo oculto)
                Text(
                    text = "?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                // Lado da frente (Conteúdo visível)
                // Invertemos a rotação do texto para compensar a rotação do card
                Text(
                    text = card.content,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    modifier = Modifier.graphicsLayer { rotationY = 180f }
                )
            }
        }
    }
}
