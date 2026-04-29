package com.personalvault.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.personalvault.app.data.expense.Budget
import com.personalvault.app.data.expense.BudgetDao
import com.personalvault.app.data.expense.CustomExpenseCategory
import com.personalvault.app.data.expense.CustomExpenseCategoryDao
import com.personalvault.app.data.expense.Expense
import com.personalvault.app.data.expense.ExpenseDao
import com.personalvault.app.data.health.HealthDao
import com.personalvault.app.data.health.HealthProfile
import com.personalvault.app.data.health.MealLog
import com.personalvault.app.data.health.SleepLog
import com.personalvault.app.data.health.WaterLog
import com.personalvault.app.data.health.WeightLog
import com.personalvault.app.data.health.WorkoutLog
import com.personalvault.app.data.sms.SmsTransaction
import com.personalvault.app.data.sms.SmsTransactionDao
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory

@Database(
    entities = [
        Entry::class,
        Expense::class,
        Budget::class,
        CustomExpenseCategory::class,
        WeightLog::class,
        MealLog::class,
        WorkoutLog::class,
        WaterLog::class,
        SleepLog::class,
        HealthProfile::class,
        SmsTransaction::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun entryDao(): EntryDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun budgetDao(): BudgetDao
    abstract fun customExpenseCategoryDao(): CustomExpenseCategoryDao
    abstract fun healthDao(): HealthDao
    abstract fun smsTransactionDao(): SmsTransactionDao

    /**
     * Clears all tables. Call on sign-out to prevent data leaking between accounts.
     */
    suspend fun clearAll() {
        clearAllTables()
    }

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: build(context).also { instance = it }
            }

        private fun build(context: Context): AppDatabase {
            val passphrase: ByteArray = DatabasePassphrase.getOrCreate(context)
            val factory: SupportSQLiteOpenHelper.Factory =
                SupportOpenHelperFactory(passphrase)

            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "personal_vault.db"
            )
                .openHelperFactory(factory)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
