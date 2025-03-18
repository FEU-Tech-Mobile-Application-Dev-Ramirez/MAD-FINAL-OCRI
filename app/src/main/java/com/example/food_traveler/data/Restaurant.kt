package com.example.food_traveler.data

data class Restaurant(
    val id: String,
    val name: String,
    val cuisine: String,
    val rating: Float,
    val priceLevel: Int,
    val imageUrl: String,
    val address: String,
    val distance: Float? = null,
    val isFavorite: Boolean = false,
    val country: String
) 