package com.example.phishingshield.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ThreatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(threat: Threat)

    @Query("SELECT * FROM threats ORDER BY createdAt DESC")
    fun all(): Flow<List<Threat>>

    @Query("SELECT * FROM threats WHERE LOWER(severity) = LOWER(:sev) ORDER BY createdAt DESC")
    fun bySeverity(sev: String): Flow<List<Threat>>
}
