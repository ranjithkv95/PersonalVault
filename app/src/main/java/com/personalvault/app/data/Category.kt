package com.personalvault.app.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class Category(
    val id: String,
    val displayName: String,
    val description: String,
    val color: Color,
    val icon: ImageVector
) {
    EXPENSES(
        id = "expenses",
        displayName = "Expenses",
        description = "Daily spending, bills, receipts",
        color = Color(0xFFFB8C00),
        icon = Icons.Default.Receipt
    ),
    HEALTH(
        id = "health",
        displayName = "Health",
        description = "Weight, meals, workouts, sleep",
        color = Color(0xFFE53935),
        icon = Icons.Default.Favorite
    );

    companion object {
        fun fromId(id: String?): Category =
            entries.firstOrNull { it.id == id } ?: EXPENSES
    }
}
