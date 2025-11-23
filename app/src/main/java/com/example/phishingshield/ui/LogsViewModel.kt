package com.example.phishingshield.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import com.example.phishingshield.data.Threat
import com.example.phishingshield.data.ThreatRepo

class LogsViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = ThreatRepo(app)
    val all = repo.all().asLiveData()
    val smishing = repo.smishing().asLiveData()
    val spam = repo.spam().asLiveData()

    // Modern LiveData extension: No Transformations needed!
    val safe = all.map { allItems: List<Threat> ->
        allItems.filter { threat ->
            !threat.severity.equals("Smishing", true) &&
                    !threat.severity.equals("Spam", true)
        }
    }
}
