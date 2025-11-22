package com.example.phishingshield.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import com.example.phishingshield.data.ThreatRepo

class LogsViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = ThreatRepo(app)
    val all = repo.all().asLiveData()
    val smishing = repo.smishing().asLiveData()
    val spam = repo.spam().asLiveData()
}
