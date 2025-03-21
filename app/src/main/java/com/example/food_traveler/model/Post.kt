package com.example.food_traveler.model

import java.util.Date

data class Post(
    val id: String,
    val userId: String,
    val title: String,
    val content: String,
    val imageUrl: String?,
    val restaurantId: String?,
    val timestamp: Date = Date(),
    val likes: Int = 0
)
