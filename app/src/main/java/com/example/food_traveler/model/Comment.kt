package com.example.food_traveler.model

import java.util.Date

data class Comment(
    val id: String,
    val postId: String,
    val userId: String,
    val content: String,
    val timestamp: Date = Date()
) 