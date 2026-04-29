package com.personalvault.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entries")
data class Entry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoryId: String,
    val title: String,
    val notes: String = "",
    val amount: Double? = null,
    val dateEpochMillis: Long = System.currentTimeMillis(),
    val attachmentsJson: String = "[]",
    val updatedAt: Long = System.currentTimeMillis()
)
