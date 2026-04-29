package com.personalvault.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {

    @Query("SELECT * FROM entries WHERE categoryId = :categoryId ORDER BY dateEpochMillis DESC, id DESC")
    fun observeByCategory(categoryId: String): Flow<List<Entry>>

    @Query("SELECT COUNT(*) FROM entries WHERE categoryId = :categoryId")
    fun countByCategory(categoryId: String): Flow<Int>

    @Query("SELECT * FROM entries WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): Entry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: Entry): Long

    @Update
    suspend fun update(entry: Entry)

    @Delete
    suspend fun delete(entry: Entry)
}
