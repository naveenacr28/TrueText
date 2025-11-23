package com.example.phishingshield.ui

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import com.example.phishingshield.R

class ComposeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        val phoneEdit = findViewById<EditText>(R.id.edit_phone)
        val msgEdit = findViewById<EditText>(R.id.edit_message)
        val sendBtn = findViewById<Button>(R.id.btn_send)

        // Prefill address/message from intent if present
        handleLaunchIntent(intent, phoneEdit, msgEdit)

        sendBtn.setOnClickListener {
            val number = phoneEdit.text.toString().trim()
            val msg = msgEdit.text.toString().trim()
            if (number.isBlank() || msg.isBlank()) {
                Toast.makeText(this, "Enter phone and message!", Toast.LENGTH_SHORT).show()
            } else {
                sendSms(number, msg)
            }
        }
    }

    // Handles intents such as sms:123456789?sms_body=Hello or smsto:...
    private fun handleLaunchIntent(intent: Intent, phoneEdit: EditText, msgEdit: EditText) {
        val data = intent.data
        val address = data?.schemeSpecificPart?.split('?')?.firstOrNull()
        val prebody = intent.getStringExtra("sms_body")
        if (!address.isNullOrBlank()) phoneEdit.setText(address)
        if (!prebody.isNullOrBlank()) msgEdit.setText(prebody)
    }

    // Actually send the SMS using system provider (works only as default SMS app)
    private fun sendSms(phoneNumber: String, messageText: String) {
        try {
            // Send the message
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, messageText, null, null)

            // Insert in the Sent folder using system provider (for compliance)
            val values = ContentValues().apply {
                put("address", phoneNumber)
                put("body", messageText)
                put("date", System.currentTimeMillis())
                put("read", 1)
                put("type", 2) // 2 = sent
            }
            contentResolver.insert(Uri.parse("content://sms/sent"), values)

            Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show()
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "SMS failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
