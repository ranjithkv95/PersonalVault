package com.personalvault.app.data.expense

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Stores user preferences for which built-in categories / subcategories are disabled.
 *
 * By default all categories are enabled. When a user disables one, its id is appended
 * to a comma-separated string. This avoids the stringSetKey API which has compatibility
 * issues across DataStore versions.
 */
private val Context.categoryPrefsStore by preferencesDataStore("category_prefs")

object CategoryPreferences {

    private val DISABLED_CATEGORIES = stringPreferencesKey("disabled_categories")
    private val DISABLED_SUBCATEGORIES = stringPreferencesKey("disabled_subcategories")

    private fun String.toSet(): Set<String> =
        if (isBlank()) emptySet() else split(",").toSet()

    private fun Set<String>.toJoined(): String = joinToString(",")

    fun observeDisabledCategories(context: Context): Flow<Set<String>> =
        context.categoryPrefsStore.data.map { prefs ->
            (prefs[DISABLED_CATEGORIES] ?: "").toSet()
        }

    fun observeDisabledSubcategories(context: Context): Flow<Set<String>> =
        context.categoryPrefsStore.data.map { prefs ->
            (prefs[DISABLED_SUBCATEGORIES] ?: "").toSet()
        }

    suspend fun toggleCategory(context: Context, categoryId: String, enabled: Boolean) {
        context.categoryPrefsStore.edit { prefs ->
            val current = (prefs[DISABLED_CATEGORIES] ?: "").toSet().toMutableSet()
            if (enabled) current.remove(categoryId) else current.add(categoryId)
            prefs[DISABLED_CATEGORIES] = current.toJoined()
        }
    }

    suspend fun toggleSubcategory(context: Context, subKey: String, enabled: Boolean) {
        context.categoryPrefsStore.edit { prefs ->
            val current = (prefs[DISABLED_SUBCATEGORIES] ?: "").toSet().toMutableSet()
            if (enabled) current.remove(subKey) else current.add(subKey)
            prefs[DISABLED_SUBCATEGORIES] = current.toJoined()
        }
    }
}
