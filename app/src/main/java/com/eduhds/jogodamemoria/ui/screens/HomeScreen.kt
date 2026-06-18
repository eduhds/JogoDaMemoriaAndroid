package com.eduhds.jogodamemoria.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.eduhds.jogodamemoria.data.GameData
import com.eduhds.jogodamemoria.model.GameCollection
import com.eduhds.jogodamemoria.model.MemoryCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onStartGame: (GameCollection) -> Unit) {
    val configuration = LocalConfiguration.current
    val columns = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 3 else 2

    val animalCards = remember {
        GameData.animalsEmojiList.mapIndexed { index, emoji ->
            MemoryCard(index, emoji)
        }
    }

    val foodCards = remember {
        GameData.foodEmojiList.mapIndexed { index, emoji ->
            MemoryCard(index, emoji)
        }
    }

    val faceCards = remember {
        GameData.facesEmojiList.mapIndexed { index, emoji ->
            MemoryCard(index, emoji)
        }
    }

    val objectCards = remember {
        GameData.objectsEmojiList.mapIndexed { index, emoji ->
            MemoryCard(index, emoji)
        }
    }

    val natureCards = remember {
        GameData.naturePlantsEmojiList.mapIndexed { index, emoji ->
            MemoryCard(index, emoji)
        }
    }

    val sportsCards = remember {
        GameData.activitiesSportsEmojiList.mapIndexed { index, emoji ->
            MemoryCard(index, emoji)
        }
    }

    val games = remember {
        listOf(
            GameCollection("Comidas", GameData.FOOD_ICON, foodCards),
            GameCollection("Animais", GameData.ANIMAL_ICON, animalCards),
            GameCollection("Expressões", GameData.FACE_ICON, faceCards),
            GameCollection("Objetos", GameData.OBJECT_ICON, objectCards),
            GameCollection("Natureza", GameData.NATURE_ICON, natureCards),
            GameCollection("Atividades", GameData.SPORTS_ICON, sportsCards)
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Jogo da Memória") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Escolha um tema para começar!",
                modifier = Modifier.padding(16.dp)
            )

            HorizontalDivider()

            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(games) { game ->
                    GameCardItem(game, onClick = { onStartGame(game) })
                }
            }
        }
    }
}

@Composable
fun GameCardItem(game: GameCollection, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .aspectRatio(1f)
            .fillMaxWidth(),
        border = CardDefaults.outlinedCardBorder(),
        elevation = CardDefaults.outlinedCardElevation(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                model = game.icon,
                contentDescription = "Game Theme",
                modifier = Modifier
                    .height(94.dp)
                    .width(94.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = game.title,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
