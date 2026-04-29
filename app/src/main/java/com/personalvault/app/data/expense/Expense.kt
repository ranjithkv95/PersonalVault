package com.personalvault.app.data.expense

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A single spend event. `dateEpochMillis` is the transaction day at local midnight.
 * `amount` is always positive (rupees). Indexed by date + category for fast aggregation.
 *
 * [remoteId] is used to de-duplicate shared expenses synced from the family group
 * in Firestore. For the user's own expenses it stays empty.
 */
@Entity(
    tableName = "expenses",
    indices = [
        Index(value = ["dateEpochMillis"]),
        Index(value = ["categoryId"]),
        Index(value = ["categoryId", "dateEpochMillis"]),
        Index(value = ["remoteId"])
    ]
)
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val categoryId: String,
    val subcategoryId: String? = null,
    val paymentMethodId: String = PaymentMethod.UPI.id,
    val merchant: String = "",
    val note: String = "",
    val dateEpochMillis: Long = System.currentTimeMillis(),
    @ColumnInfo(defaultValue = "[]") val attachmentsJson: String = "[]",
    @ColumnInfo(defaultValue = "0") val isRecurring: Boolean = false,
    @ColumnInfo(defaultValue = "") val tags: String = "",
    @ColumnInfo(defaultValue = "0") val isShared: Boolean = false,
    @ColumnInfo(defaultValue = "") val addedByUid: String = "",
    @ColumnInfo(defaultValue = "") val addedByName: String = "",
    @ColumnInfo(defaultValue = "") val remoteId: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
