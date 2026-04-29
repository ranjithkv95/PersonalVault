package com.personalvault.app.data.expense

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A monthly budget. When [categoryId] is null the row represents the overall
 * monthly cap; otherwise it's a per-category cap.
 * [monthKey] is "YYYY-MM" (local timezone).
 *
 * [isShared] separates personal budgets (false) from combined/family budgets (true).
 * This allows users to set different caps for "My Expenses" vs "Combined".
 */
@Entity(
    tableName = "budgets",
    indices = [Index(value = ["monthKey", "categoryId", "isShared"], unique = true)]
)
data class Budget(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val monthKey: String,          // e.g. "2026-04"
    val categoryId: String? = null, // null = overall
    val amount: Double,
    @ColumnInfo(defaultValue = "0") val isShared: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
