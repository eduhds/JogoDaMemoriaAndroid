package com.eduhds.jogodamemoria.model

data class MemoryCard(
    val id: Int,
    val content: String,
    val isFlipped: Boolean = false,
    val isMatched: Boolean = false
)
