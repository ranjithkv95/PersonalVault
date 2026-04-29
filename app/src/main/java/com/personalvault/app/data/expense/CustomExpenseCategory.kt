package com.personalvault.app.data.expense

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * User-defined custom category or subcategory.
 *
 * - When [parentCategoryId] is null  → it is a top-level custom *category* that
 *   appears alongside the built-in 20 categories in pickers.
 * - When [parentCategoryId] is a valid built-in or custom category id → it is a
 *   custom *subcategory* that shows up inside that parent.
 */
@Entity(
    tableName = "custom_expense_categories",
    indices = [Index(value = ["parentCategoryId"])]
)
data class CustomExpenseCategory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val parentCategoryId: String? = null,
    val colorHex: String = "#616161",
    val iconKey: String = "tag",
    val createdAt: Long = System.currentTimeMillis()
) {
    /** String id used inside Expense rows so both built-in and custom entries coexist. */
    fun asCategoryId(): String = "custom:$id"
    fun asSubcategoryId(): String = "custom_sub:$id"
}

@Dao
interface CustomExpenseCategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CustomExpenseCategory): Long

    @Update
    suspend fun update(item: CustomExpenseCategory)

    @Delete
    suspend fun delete(item: CustomExpenseCategory)

    @Query("SELECT * FROM custom_expense_categories ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<CustomExpenseCategory>>

    @Query("SELECT * FROM custom_expense_categories WHERE parentCategoryId IS NULL ORDER BY name")
    fun observeTopLevel(): Flow<List<CustomExpenseCategory>>

    @Query("SELECT * FROM custom_expense_categories WHERE parentCategoryId = :parentId ORDER BY name")
    fun observeSubcategoriesOf(parentId: String): Flow<List<CustomExpenseCategory>>
}
