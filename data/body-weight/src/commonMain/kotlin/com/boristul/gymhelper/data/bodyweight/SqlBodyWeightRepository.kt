package com.boristul.gymhelper.data.bodyweight

import com.boristul.gymhelper.database.GymHelperDatabase
import com.boristul.gymhelper.domain.bodyweight.BodyWeightEntry
import com.boristul.gymhelper.domain.bodyweight.BodyWeightRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class SqlBodyWeightRepository(
    private val database: GymHelperDatabase,
) : BodyWeightRepository {
    private val queries = database.gymHelperQueries
    private val entries = MutableStateFlow(loadEntries())

    override fun observeEntries(): Flow<List<BodyWeightEntry>> = entries

    override suspend fun saveTodayWeight(weightKg: Double) {
        queries.upsertTodayBodyWeight(weightKg)
        entries.value = loadEntries()
    }

    override suspend fun deleteEntry(entryId: String) {
        queries.deleteBodyWeightEntry(entryId)
        entries.value = loadEntries()
    }

    private fun loadEntries(): List<BodyWeightEntry> {
        return queries
            .selectBodyWeightEntries()
            .executeAsList()
            .map { entry ->
                BodyWeightEntry(
                    id = entry.id,
                    date = entry.entry_date,
                    weightKg = entry.weight_kg,
                )
            }
    }
}
