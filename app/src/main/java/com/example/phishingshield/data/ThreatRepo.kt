package com.example.phishingshield.data

import android.content.Context
import kotlinx.coroutines.flow.Flow

class ThreatRepo(ctx: Context) {
    private val dao = AppDb.get(ctx).dao()
    suspend fun add(t: Threat) = dao.insert(t)
    fun all(): Flow<List<Threat>> = dao.all()
    fun smishing(): Flow<List<Threat>> = dao.bySeverity("Smishing")
    fun spam(): Flow<List<Threat>> = dao.bySeverity("Spam")
}
