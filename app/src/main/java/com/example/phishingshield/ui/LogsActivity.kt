package com.example.phishingshield.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.phishingshield.R
import com.google.android.material.tabs.TabLayout

class LogsActivity : ComponentActivity() {
    private val vm: LogsViewModel by viewModels()
    private lateinit var adapter: ThreatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logs)

        val tabs = findViewById<TabLayout>(R.id.tabs)
        val list = findViewById<RecyclerView>(R.id.list)
        adapter = ThreatAdapter()
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter

        tabs.addTab(tabs.newTab().setText("All"))
        tabs.addTab(tabs.newTab().setText("Smishing"))
        tabs.addTab(tabs.newTab().setText("Spam"))

        fun bindAll() { vm.all.observe(this) { adapter.submitList(it) } }
        fun bindSmish() { vm.smishing.observe(this) { adapter.submitList(it) } }
        fun bindSpam() { vm.spam.observe(this) { adapter.submitList(it) } }

        bindAll()
        tabs.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(t: TabLayout.Tab) {
                when (t.position) {
                    0 -> bindAll()
                    1 -> bindSmish()
                    2 -> bindSpam()
                }
            }
            override fun onTabUnselected(t: TabLayout.Tab) {}
            override fun onTabReselected(t: TabLayout.Tab) {}
        })
    }
}
