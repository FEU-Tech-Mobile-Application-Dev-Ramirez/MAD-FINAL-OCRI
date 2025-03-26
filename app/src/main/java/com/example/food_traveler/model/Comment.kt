package com.example.food_traveler.model

data class Comment(
    val id: String,
    val postId: String,
    val userId: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
) 