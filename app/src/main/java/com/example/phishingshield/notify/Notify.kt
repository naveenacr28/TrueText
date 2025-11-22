package com.example.phishingshield.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.phishingshield.MainActivity
import com.example.phishingshield.R
import java.util.Locale

object Notify {
    private const val CHANNEL_ID = "truetext_alerts"
    private const val CHANNEL_NAME = "TrueText Alerts"
    private const val CHANNEL_DESC = "Threat alerts from TrueText"

    private fun ensureChannel(ctx: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC
                enableVibration(true)
                enableLights(true)
            }
            ctx.getSystemService(NotificationManager::class.java)?.createNotificationChannel(ch)
        }
    }

    fun show(ctx: Context, severity: String, conf: Double, latencyMs: Int, preview: String) {
        ensureChannel(ctx)
        val nm = NotificationManagerCompat.from(ctx)
        if (!nm.areNotificationsEnabled()) return

        val isSmish = severity.equals("Smishing", true)
        val layoutId = if (isSmish) R.layout.notify_smishing else R.layout.notify_spam
        val rv = RemoteViews(ctx.packageName, layoutId)

        val confText = "Confidence " + String.format(Locale.US, "%.2f", conf)
        rv.setTextViewText(R.id.brand, "TrueText")
        rv.setTextViewText(R.id.title, if (isSmish) "Smishing Detected" else "Spam Detected")
        rv.setTextViewText(R.id.line1, "$confText • ${latencyMs}ms")
        rv.setTextViewText(R.id.message, preview.take(180))
        rv.setImageViewResource(R.id.logo, R.drawable.logo_truetext_background)
        rv.setImageViewResource(R.id.badge, if (isSmish) R.drawable.ic_danger_red else R.drawable.ic_warning_yellow)

        val smallIcon = if (isSmish) R.drawable.ic_danger_red else R.drawable.ic_warning_yellow

        // ---------- NEW: Launch app when notification tapped -----------
        val launchIntent = Intent(ctx, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Optionally add more extras to pass threat data if wanted
        }
        val pendingIntent = PendingIntent.getActivity(
            ctx, 0, launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // For API 23+
        )

        val builder = NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setSmallIcon(smallIcon)
            .setCustomContentView(rv)
            .setCustomBigContentView(rv)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 400, 200, 400))
            .setContentIntent(pendingIntent) // <-- This line enables tap-to-open-app

        nm.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}
