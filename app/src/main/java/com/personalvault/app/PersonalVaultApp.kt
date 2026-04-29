package com.personalvault.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.personalvault.app.data.AppDatabase
import com.personalvault.app.data.EntryRepository
import com.personalvault.app.data.auth.AuthRepository
import com.personalvault.app.data.expense.ExpenseRepository
import com.personalvault.app.data.family.FamilyRepository
import com.personalvault.app.data.health.HealthRepository
import com.personalvault.app.data.sms.SmsTransactionRepository
import com.personalvault.app.data.sync.FirestoreSyncManager
import com.personalvault.app.notification.ExpenseReminderReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class PersonalVaultApp : Application() {

    /** Application-scoped coroutine scope that survives composable disposal. */
    val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database: AppDatabase by lazy { AppDatabase.get(this) }
    val repository: EntryRepository by lazy { EntryRepository(database.entryDao()) }
    val expenseRepository: ExpenseRepository by lazy {
        ExpenseRepository(
            database.expenseDao(),
            database.budgetDao(),
            database.customExpenseCategoryDao()
        )
    }
    val healthRepository: HealthRepository by lazy {
        HealthRepository(database.healthDao())
    }

    val authRepository: AuthRepository by lazy { AuthRepository() }
    val syncManager: FirestoreSyncManager by lazy { FirestoreSyncManager(database) }
    val familyRepository: FamilyRepository by lazy { FamilyRepository() }
    val smsTransactionRepository: SmsTransactionRepository by lazy {
        SmsTransactionRepository(database.smsTransactionDao(), expenseRepository)
    }

    override fun onCreate() {
        super.onCreate()
        System.loadLibrary("sqlcipher")
        createNotificationChannels()
    }

    /** Sign out: stop sync, clear local data, sign out of Firebase. Safe to call from any scope. */
    suspend fun performSignOut() {
        syncManager.stopListening()
        database.clearAll()
        authRepository.signOut()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ExpenseReminderReceiver.CHANNEL_ID,
                "Expense Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminds you to add your daily expenses"
            }
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }
    }
}
