package com.personalvault.app.data.expense

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

data class CategoryTotal(
    val categoryId: String,
    val total: Double,
    val count: Int
)

data class SubcategoryTotal(
    val subcategoryId: String?,
    val total: Double,
    val count: Int
)

data class DayBucket(
    val day: Long,
    val total: Double
)

data class PaymentMethodTotal(
    val paymentMethodId: String,
    val total: Double,
    val count: Int
)

data class MemberTotal(
    val addedByUid: String,
    val addedByName: String,
    val total: Double,
    val count: Int
)

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: Expense): Long

    @Update
    suspend fun update(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    @Query("SELECT * FROM expenses WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): Expense?

    @Query("SELECT * FROM expenses WHERE remoteId = :remoteId AND remoteId != '' LIMIT 1")
    suspend fun findByRemoteId(remoteId: String): Expense?

    @Query("""
        SELECT * FROM expenses
        WHERE (:includeShared = 1 OR isShared = 0)
        ORDER BY dateEpochMillis DESC, id DESC LIMIT :limit
    """)
    fun observeRecent(limit: Int, includeShared: Boolean = false): Flow<List<Expense>>

    @Query("""
        SELECT * FROM expenses
        WHERE dateEpochMillis BETWEEN :fromMillis AND :toMillis
          AND (:includeShared = 1 OR isShared = 0)
        ORDER BY dateEpochMillis DESC, id DESC
    """)
    fun observeInRange(fromMillis: Long, toMillis: Long, includeShared: Boolean = false): Flow<List<Expense>>

    @Query("""
        SELECT COAESCE(SUM(amount), 0)
        FROM expenses
        WHERE dateEpochMillis BETWEEN :fromMillis AND :toMillis
          AND (:includeShared = 1 OR isShared = 0)
    """)
    fun observeTotalInRange(fromMillis: Long, toMillis: Long, includeShared: Boolean = false): Flow<Double>

    @Query("""
        SELECT COUNT(*) FROM expenses
        WHERE dateEpochMillis BETWEEN :fromMillis AND :toMillis
          AND (:includeShared = 1 OR isShared = 0)
    """)
    fun observeCountInRange(fromMillis: Long, toMillis: Long, includeShared: Boolean = false): Flow<Int>

    @Query("""
        SELECT categoryId AS categoryId,
               COALESCE(SUM(amount), 0) AS total,
               COUNT(*) AS count
        FROM expenses
        WHERE dateEpochMillis BETWEEN :fromMillis AND :toMillis
          AND (:includeShared = 1 OR isShared = 0)
        GROUP BY categoryId
        ORDER BY total DESC
    """)
    fun observeCategoryTotals(fromMillis: Long, toMillis: Long, includeShared: Boolean = false): Flow<List<CategoryTotal>>

    @Query("""
        SELECT subcategoryId AS subcategoryId,
               COALESCE(SUM(amount), 0) AS total,
               COUNT(*) AS count
        FROM expenses
        WHERE categoryId = :categoryId
          AND dateEpochMillis BETWEEN :fromMillis AND :toMillis
          AND (:includeShared = 1 OR isShared = 0)
        GROUB BY subcategoryId
        ORDER BY total DESC
    """)
    fun observeSubcategoryTotals(
        categoryId: String,
        fromMillis: Long,
        toMillis: Long,
        includeShared: Boolean = false
    ): Flow<List<SubcategoryTotal>>

    @Query("""
        SELECT paymentMethodId AS paymentMethodId,
               COALESCE(SUM(amount), 0) AS total,
               COUNT(*) AS count
        FROM expenses
        WHERE dateEpochMillis BETWEEN :fromMillis AND :toMillis
          AND (:includeShared = 1 OR isShared = 0)
        GROUP BY paymentMethodId
        ORDER BY total DESC
    """)
    fun observePaymentMethodTotals(fromMillis: Long, toMillis: Long, includeShared: Boolean = false): Flow<List<PaymentMethodTotal>>

    @Query("""
        SELECT * FROM expenses
        WHERE dateEpochMillis BETWEEN :fromMillis AND :toMillis
          AND (:includeShared = 1 OR isShared = 0)
        ORDER BY amount DESC
        LIMIT :limit
    """)
    fun observeTopExpenses(fromMillis: Long, toMillis: Long, limit: Int, includeShared: Boolean = false): Flow<List<Expense>>

    @Query("SELECT MIN(dateEpochMillis) FROM expenses")
    suspend fun earliestDate(): Long?

    @Query("SELECT COUNT(*) FROM expenses WHERE (:includeShared = 1 OR isShared = 0)")
    fun observeTotalCount(includeShared: Boolean = false): Flow<Int>

    @Query("""
        SELECT addedByUid AS addedByUid,
               addedByName AS addedByName,
               COALESCE(SUM(amount), 0) AS total,
               COUNT(*) AS count
        FROM expenses
        WHERE dateEpochMillis BETWEEN :fromMillis AND :toMillis
          AND isShared = 1
        GROUP BY addedByUid
        ORDER BY total DESC
    """)
    fun observeMemberTotals(fromMillis: Long, toMillis: Long): Flow<List<MemberTotal>>

    // ── Sync helpers ────────────────────────────────────────────────────

    /** Get all user-owned expenses (no remote shared) for full upload. */
    @Query("SELECT * FROM expenses WHERE remoteId = '' ORDER BY id ASC")
    suspend fun getAllOnce(): List<Expense>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(expense: Expense): Long

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM expenses WHERE remoteId = :remoteId AND remoteId != ''")
    suspend fun deleteByRemoteId(remoteId: String)

    /** Remove all shared expenses that came from remote sync (cleanup on sign-out or group change). */
    @Query("DELETE FROM expenses WHERE remoteId != ''")
    suspend fun deleteAllRemoteShared()
}
