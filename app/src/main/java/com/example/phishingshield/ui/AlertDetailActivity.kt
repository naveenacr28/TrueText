package com.example.phishingshield.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.phishingshield.R
import java.text.SimpleDateFormat
import java.util.*

class AlertDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert_detail)

        val severity = intent.getStringExtra("severity")
        val confidence = intent.getDoubleExtra("confidence", 0.0)
        val latencyMs = intent.getIntExtra("latencyMs", 0)
        val message = intent.getStringExtra("message")
        val createdAt = intent.getLongExtra("createdAt", 0L)

        findViewById<TextView>(R.id.detail_severity).text = severity
        findViewById<TextView>(R.id.detail_conf).text = "Confidence %.2f • %dms".format(confidence, latencyMs)
        // 12-hour format with AM/PM
        val df = SimpleDateFormat("MMM d, hh:mm a", Locale.getDefault())
        findViewById<TextView>(R.id.detail_time).text = df.format(Date(createdAt))
        findViewById<TextView>(R.id.detail_msg).text = message
    }
}
