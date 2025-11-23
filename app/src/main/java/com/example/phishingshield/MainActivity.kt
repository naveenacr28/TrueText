package com.example.phishingshield

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.phishingshield.ui.ComposeActivity
import com.example.phishingshield.ui.ListFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private val reqPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        android.util.Log.w("MainActivity", "ping=" + com.example.phishingshield.net.ApiClient.ping())
        requestNeededPermissions()

        val pager = findViewById<androidx.viewpager2.widget.ViewPager2>(R.id.pager)
        val tabs = findViewById<TabLayout>(R.id.tabs)

        pager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 3
            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> ListFragment.newInstance("Safe")
                    1 -> ListFragment.newInstance("Smishing")
                    else -> ListFragment.newInstance("Spam")
                }
            }
        }

        TabLayoutMediator(tabs, pager) { tab, pos ->
            tab.text = arrayOf("Safe", "Smishing", "Spam")[pos]
        }.attach()

        handleSystemSmsIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleSystemSmsIntent(intent)
    }

    private fun handleSystemSmsIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_SENDTO || intent.action == Intent.ACTION_VIEW) {
            val data = intent.data
            val address = data?.schemeSpecificPart
            val message = intent.getStringExtra("sms_body")
            // Launch ComposeActivity with address and message
            val compose = Intent(this, ComposeActivity::class.java)
            compose.putExtra("phone", address)
            compose.putExtra("body", message)
            startActivity(compose)
        }
    }

    private fun requestNeededPermissions() {
        val perms = mutableListOf(
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS
        )
        if (Build.VERSION.SDK_INT >= 33) perms += Manifest.permission.POST_NOTIFICATIONS
        val need = perms.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (need.isNotEmpty()) reqPermissions.launch(need.toTypedArray())
    }
}
