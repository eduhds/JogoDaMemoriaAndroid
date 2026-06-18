package com.eduhds.jogodamemoria.model

data class GameCollection (
    val title: String,
    val icon: String,
    val cards: List<MemoryCard>
)