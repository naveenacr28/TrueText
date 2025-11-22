package com.example.phishingshield.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.example.phishingshield.notify.Notify
import com.example.phishingshield.net.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Probe log to ensure delivery
        android.util.Log.w("SmsReceiver", "TEST fired action=${intent.action}")

        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION != intent.action) return
        android.util.Log.i("SmsReceiver", "1 action=${intent.action}")

        // Parse using framework helper
        val msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        val fullText = msgs.joinToString(" ") { it.displayMessageBody ?: "" }.trim()
        if (fullText.isEmpty()) return
        android.util.Log.i("SmsReceiver", "2 text=${fullText.take(80)}")

        CoroutineScope(Dispatchers.IO).launch {
            val raw = try { ApiClient.postPredict(fullText) } catch (e: Exception) {
                android.util.Log.e("SmsReceiver", "http error", e); null
            }
            android.util.Log.i("SmsReceiver", "3 raw=$raw")
            if (raw.isNullOrBlank()) return@launch

            val json = try { JSONObject(raw) } catch (_: Exception) { return@launch }
            val pred = json.optString("prediction", "")
            val conf = json.optDouble("confidence", 0.0)
            val latency = json.optInt("latency_ms", 0)

            // Normalize prediction text robustly
            val p = pred.lowercase()
            val severity = when {
                "smish" in p || "phish" in p -> "Smishing"
                "spam" in p -> "Spam"
                else -> "Suspicious"
            }
            if (severity == "Suspicious" && p.contains("ham")) return@launch

            android.util.Log.i("SmsReceiver", "4 notify pred=$pred severity=$severity conf=$conf")

            Notify.show(
                ctx = context,
                severity = severity,
                conf = conf,
                latencyMs = latency,
                preview = fullText
            )
            try {
                com.example.phishingshield.data.ThreatRepo(context).add(
                    com.example.phishingshield.data.Threat(
                        severity = severity,
                        confidence = conf,
                        latencyMs = latency,
                        message = fullText,
                        createdAt = System.currentTimeMillis()
                    )
                )
            } catch (_: Exception) { }
        }
    }
}
