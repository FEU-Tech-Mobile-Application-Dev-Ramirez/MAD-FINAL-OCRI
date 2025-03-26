package com.example.food_traveler.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RatingBar(
    rating: Float,
    onRatingChanged: (Float) -> Unit,
    isInteractive: Boolean = false
) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        repeat(5) { index ->
            val starRating = index + 1
            IconButton(
                onClick = { if (isInteractive) onRatingChanged(starRating.toFloat()) },
                enabled = isInteractive
            ) {
                Icon(
                    imageVector = if (starRating <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Star $starRating",
                    tint = if (starRating <= rating) 
                          MaterialTheme.colorScheme.secondary 
                          else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                )
            }
        }
    }
} 