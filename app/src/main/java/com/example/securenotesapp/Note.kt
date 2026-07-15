package com.example.securenotesapp

/**
 * Data class representing a Note entity with title, subtitle, and content.
 */
data class Note(
    val id: Int,
    val title: String,
    val subtitle: String,
    val content: String
)
