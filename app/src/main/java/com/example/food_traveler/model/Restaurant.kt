package com.example.food_traveler.model

data class Restaurant(
    val id: String,
    val name: String,
    val cuisine: String,
    val rating: Float,
    val priceLevel: Int, // 1-4, representing $-$$$$
    val imageUrl: String,
    val address: String,
    val distance: Float? = null, // in kilometers
    val isFavorite: Boolean = false,
    val country: String // New property
) 