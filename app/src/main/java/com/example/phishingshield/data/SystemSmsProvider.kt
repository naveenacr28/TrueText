package com.example.phishingshield.data

import android.content.Context
import android.database.Cursor
import android.net.Uri

object SystemSmsProvider {
    fun getInboxSms(context: Context): Cursor? {
        val uri = Uri.parse("content://sms/inbox")
        return context.contentResolver.query(uri, null, null, null, "date DESC")
    }
}
