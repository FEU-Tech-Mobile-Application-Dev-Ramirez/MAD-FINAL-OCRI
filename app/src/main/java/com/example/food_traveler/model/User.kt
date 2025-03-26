package com.example.food_traveler.model

data class User(
    val id: String,
    val email: String,
    val displayName: String,
    val profileImageUrl: String? = null,
    val isAdmin: Boolean = false
)
