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

        val layoutId = when {
            severity.equals("Smishing", true) -> R.layout.notify_smishing
            severity.equals("Spam", true) -> R.layout.notify_spam
            else -> R.layout.notify_safe // <-- you must create this minimal layout!
        }

        val rv = RemoteViews(ctx.packageName, layoutId)

        val confText = "Confidence " + String.format(Locale.US, "%.2f", conf)
        rv.setTextViewText(R.id.brand, "TrueText")
        rv.setTextViewText(R.id.title,
            when {
                severity.equals("Smishing", true) -> "Smishing Detected"
                severity.equals("Spam", true) -> "Spam Detected"
                else -> "Safe Message"
            }
        )
        rv.setTextViewText(R.id.line1, "$confText • ${latencyMs}ms")
        rv.setTextViewText(R.id.message, preview.take(180))
        rv.setImageViewResource(R.id.logo, R.drawable.logo_truetext_background)
        rv.setImageViewResource(R.id.badge,
            when {
                severity.equals("Smishing", true) -> R.drawable.ic_danger_red
                severity.equals("Spam", true) -> R.drawable.ic_warning_yellow
                else -> R.drawable.ic_check_green // <-- use green check or badge icon
            }
        )

        val smallIcon = when {
            severity.equals("Smishing", true) -> R.drawable.ic_danger_red
            severity.equals("Spam", true) -> R.drawable.ic_warning_yellow
            else -> R.drawable.ic_check_green // <-- use green icon for Safe!
        }

        val launchIntent = Intent(ctx, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
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
            .setContentIntent(pendingIntent)

        nm.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}
