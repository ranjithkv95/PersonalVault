package com.personalvault.app.data

import kotlinx.coroutines.flow.Flow

class EntryRepository(private val dao: EntryDao) {

    fun observeByCategory(categoryId: String): Flow<List<Entry>> =
        dao.observeByCategory(categoryId)

    fun countByCategory(categoryId: String): Flow<Int> =
        dao.countByCategory(categoryId)

    suspend fun findById(id: Long): Entry? = dao.findById(id)

    suspend fun upsert(entry: Entry): Long =
        if (entry.id == 0L) dao.insert(entry)
        else { dao.update(entry); entry.id }

    suspend fun delete(entry: Entry) = dao.delete(entry)
}
