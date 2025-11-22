package com.example.phishingshield.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "threats")
data class Threat(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val severity: String,     // "Smishing" | "Spam" | "Suspicious"
    val confidence: Double,
    val latencyMs: Int,
    val message: String,
    val createdAt: Long       // System.currentTimeMillis()
)
