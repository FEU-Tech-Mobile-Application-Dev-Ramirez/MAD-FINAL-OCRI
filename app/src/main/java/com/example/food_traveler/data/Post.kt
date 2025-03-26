package com.example.food_traveler.data

import java.util.Date

data class Post(
    val id: String,
    val userId: String,
    val userName: String,
    val content: String,
    val imageUrl: String? = null,
    val timestamp: Date = Date(),
    val ratingCount: Int = 0,
    val averageRating: Float = 0f,
    val comments: List<String> = emptyList(),
    val isApproved: Boolean = false
) 