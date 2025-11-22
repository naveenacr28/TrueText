package com.example.phishingshield.net

import android.util.Log
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

object ApiClient {
    // Use LAN for local; swap to ngrok HTTPS when needed
    // Example: "http://10.91.171.44:5000" or "https://YOUR-NGROK-URL"
    private const val BASE = "https://moshe-kinetic-mendaciously.ngrok-free.dev"

    private val jsonMedia = "application/json; charset=utf-8".toMediaType()

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .callTimeout(15, TimeUnit.SECONDS)
        .build()

    fun ping(): Boolean {
        val url = "$BASE/health"
        val req = Request.Builder().url(url).get().build()
        return try {
            client.newCall(req).execute().use { resp ->
                Log.w("ApiClient", "GET /health -> ${resp.code}")
                resp.isSuccessful
            }
        } catch (e: Exception) {
            Log.e("ApiClient", "health error: ${e.javaClass.simpleName}: ${e.message}")
            false
        }
    }

    fun postPredict(text: String): String? {
        val url = "$BASE/predict"
        val body = """{"text":${escapeJson(text)}}""".toRequestBody(jsonMedia)
        Log.w("ApiClient", "POST /predict to $url")
        val req = Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .build()
        return try {
            client.newCall(req).execute().use { resp ->
                val s = resp.body?.string()
                Log.w("ApiClient", "resp ${resp.code} ${s?.take(200)}")
                if (resp.isSuccessful) s else null
            }
        } catch (e: Exception) {
            Log.e("ApiClient", "http error: ${e.javaClass.simpleName}: ${e.message}")
            return null
        }
    }

    // Minimal JSON escaper
    private fun escapeJson(s: String): String {
        val sb = StringBuilder("\"")
        for (ch in s) {
            when (ch) {
                '\\' -> sb.append("\\\\")
                '"'  -> sb.append("\\\"")
                '\b' -> sb.append("\\b")
                '\n' -> sb.append("\\n")
                '\r' -> sb.append("\\r")
                '\t' -> sb.append("\\t")
                '\u000C' -> sb.append("\\f")
                else -> {
                    if (ch.code < 0x20) sb.append(String.format("\\u%04x", ch.code))
                    else sb.append(ch)
                }
            }
        }
        sb.append('"')
        return sb.toString()
    }
}
