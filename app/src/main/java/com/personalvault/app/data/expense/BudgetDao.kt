package com.personalvault.app.data.expense

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(budget: Budget): Long

    @Delete
    suspend fun delete(budget: Budget)

    @Query("""
        SELECT * FROM budgets
        WHERE monthKey = :monthKey AND isShared = :isShared
        ORDER BY categoryId IS NULL DESC
    """)
    fun observeForMonth(monthKey: String, isShared: Boolean = false): Flow<List<Budget>>

    @Query("""
        SELECT * FROM budgets
        WHERE monthKey = :monthKey AND categoryId IS NULL AND isShared = :isShared
        LIMIT 1
    """)
    fun observeOverallForMonth(monthKey: String, isShared: Boolean = false): Flow<Budget?>

    @Query("SELECT * FROM budgets WHERE monthKey = :monthKey AND categoryId = :categoryId LIMIT 1")
    suspend fun findCategoryBudget(monthKey: String, categoryId: String): Budget?

    /** Find a budget row handling NULL categoryId correctly (SQLite NULL != NULL). */
    @Query("""
        SELECT * FROM budgets
        WHERE monthKey = :monthKey
          AND (categoryId = :categoryId OR (categoryId IS NULL AND :categoryId IS NULL))
          AND isShared = :isShared
        LIMIT 1
    """)
    suspend fun findBudget(monthKey: String, categoryId: String?, isShared: Boolean = false): Budget?
}
