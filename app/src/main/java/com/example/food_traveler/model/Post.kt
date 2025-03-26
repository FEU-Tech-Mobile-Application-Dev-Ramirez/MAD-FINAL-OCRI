package com.example.food_traveler.model

import java.util.Date

enum class PostStatus {
    PENDING,
    APPROVED,
    REJECTED
}

data class Post(
    val id: String,
    val userId: String,
    val userName: String,
    val title: String,
    val content: String,
    val restaurantId: String? = null,
    val imageUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val status: PostStatus = PostStatus.PENDING,
    val ratingCount: Int = 0,
    val averageRating: Float = 0f,
    val likes: Int = 0,
    val location: String? = null,
    val ratings: List<Float> = emptyList()
)
