package com.personalvault.app.data.expense

import kotlinx.coroutines.flow.Flow

class ExpenseRepository(
    private val expenseDao: ExpenseDao,
    private val budgetDao: BudgetDao,
    private val customCategoryDao: CustomExpenseCategoryDao
) {
    // ---- Custom categories --------------------------------------------------

    fun observeCustomCategories(): Flow<List<CustomExpenseCategory>> =
        customCategoryDao.observeAll()

    fun observeCustomTopLevel(): Flow<List<CustomExpenseCategory>> =
        customCategoryDao.observeTopLevel()

    fun observeCustomSubcategoriesOf(parentId: String): Flow<List<CustomExpenseCategory>> =
        customCategoryDao.observeSubcategoriesOf(parentId)

    suspend fun upsertCustomCategory(item: CustomExpenseCategory): Long =
        if (item.id == 0L) customCategoryDao.insert(item) else {
            customCategoryDao.update(item); item.id
        }

    suspend fun deleteCustomCategory(item: CustomExpenseCategory) =
        customCategoryDao.delete(item)

    // ---- Writes --------------------------------------------------------------

    suspend fun upsert(expense: Expense): Long =
        if (expense.id == 0L) expenseDao.insert(expense)
        else { expenseDao.update(expense); expense.id }

    suspend fun delete(expense: Expense) = expenseDao.delete(expense)
    suspend fun findById(id: Long): Expense? = expenseDao.findById(id)

    // ---- Queries -------------------------------------------------------------

    fun observeRecent(limit: Int = 20, includeShared: Boolean = false): Flow<List<Expense>> =
        expenseDao.observeRecent(limit, includeShared)

    fun observeInRange(from: Long, to: Long, includeShared: Boolean = false): Flow<List<Expense>> =
        expenseDao.observeInRange(from, to, includeShared)

    fun observeTotalInRange(from: Long, to: Long, includeShared: Boolean = false): Flow<Double> =
        expenseDao.observeTotalInRange(from, to, includeShared)

    fun observeCountInRange(from: Long, to: Long, includeShared: Boolean = false): Flow<Int> =
        expenseDao.observeCountInRange(from, to, includeShared)

    fun observeCategoryTotals(from: Long, to: Long, includeShared: Boolean = false): Flow<List<CategoryTotal>> =
        expenseDao.observeCategoryTotals(from, to, includeShared)

    fun observeSubcategoryTotals(
        categoryId: String, from: Long, to: Long, includeShared: Boolean = false
    ): Flow<List<SubcategoryTotal>> =
        expenseDao.observeSubcategoryTotals(categoryId, from, to, includeShared)

    fun observePaymentMethodTotals(from: Long, to: Long, includeShared: Boolean = false): Flow<List<PaymentMethodTotal>> =
        expenseDao.observePaymentMethodTotals(from, to, includeShared)

    fun observeTopExpenses(from: Long, to: Long, limit: Int = 5, includeShared: Boolean = false): Flow<List<Expense>> =
        expenseDao.observeTopExpenses(from, to, limit, includeShared)

    suspend fun earliestDate(): Long? = expenseDao.earliestDate()

    fun observeTotalCount(includeShared: Boolean = false): Flow<Int> = expenseDao.observeTotalCount(includeShared)

    fun observeMemberTotals(from: Long, to: Long): Flow<List<MemberTotal>> =
        expenseDao.observeMemberTotals(from, to)

    // ---- Budgets -------------------------------------------------------------

    suspend fun setBudget(monthKey: String, categoryId: String?, amount: Double, isShared: Boolean = false) {
        // Look up existing row first — the unique index doesn't catch NULL == NULL in SQLite
        val existing = budgetDao.findBudget(monthKey, categoryId, isShared)
        val budget = if (existing != null) {
            existing.copy(amount = amount, updatedAt = System.currentTimeMillis())
        } else {
            Budget(monthKey = monthKey, categoryId = categoryId, amount = amount, isShared = isShared)
        }
        budgetDao.upsert(budget)
    }

    fun observeBudgetsForMonth(monthKey: String, isShared: Boolean = false): Flow<List<Budget>> =
        budgetDao.observeForMonth(monthKey, isShared)

    fun observeOverallBudget(monthKey: String, isShared: Boolean = false): Flow<Budget?> =
        budgetDao.observeOverallForMonth(monthKey, isShared)
}
