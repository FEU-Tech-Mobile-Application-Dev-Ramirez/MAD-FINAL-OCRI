package com.example.food_traveler.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.food_traveler.R

sealed class NavigationItem(
    val route: String,
    val title: Int,
    val icon: ImageVector
) {
    object Discover : NavigationItem(
        route = "discover",
        title = R.string.discover,
        icon = Icons.Default.Explore
    )
    
    object Reviews : NavigationItem(
        route = "reviews",
        title = R.string.reviews,
        icon = Icons.Default.Star
    )
    
    object Profile : NavigationItem(
        route = "profile",
        title = R.string.profile,
        icon = Icons.Default.Person
    )
    
    object Admin : NavigationItem(
        route = "admin",
        title = R.string.admin,
        icon = Icons.Default.AdminPanelSettings
    )
} 