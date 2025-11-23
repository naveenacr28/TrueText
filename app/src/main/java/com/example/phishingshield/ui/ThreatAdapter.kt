package com.example.phishingshield.ui

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.phishingshield.R
import com.example.phishingshield.data.Threat
import java.text.SimpleDateFormat
import java.util.*

class ThreatAdapter : ListAdapter<Threat, ThreatAdapter.H>(DIFF) {
    object DIFF : DiffUtil.ItemCallback<Threat>() {
        override fun areItemsTheSame(o: Threat, n: Threat) = o.id == n.id
        override fun areContentsTheSame(o: Threat, n: Threat) = o == n
    }
    class H(val root: ViewGroup) : RecyclerView.ViewHolder(root) {
        val title: TextView = root.findViewById(R.id.row_title)
        val sub: TextView = root.findViewById(R.id.row_sub)
        val msg: TextView = root.findViewById(R.id.row_msg)
    }
    override fun onCreateViewHolder(p: ViewGroup, t: Int): H {
        val v = LayoutInflater.from(p.context).inflate(R.layout.row_threat, p, false) as ViewGroup
        return H(v)
    }
    override fun onBindViewHolder(h: H, i: Int) {
        val x = getItem(i)
        val df = SimpleDateFormat("MMM d, hh:mm a", Locale.getDefault())
        val time = df.format(Date(x.createdAt))
        h.title.text = x.severity
        h.sub.text = "Confidence ${"%.2f".format(x.confidence)} • ${x.latencyMs}ms • $time"
        h.msg.text = x.message

        h.root.setOnClickListener {
            val ctx = h.root.context
            val intent = Intent(ctx, AlertDetailActivity::class.java)
            intent.putExtra("severity", x.severity)
            intent.putExtra("confidence", x.confidence)
            intent.putExtra("latencyMs", x.latencyMs)
            intent.putExtra("message", x.message)
            intent.putExtra("createdAt", x.createdAt)
            ctx.startActivity(intent)
        }
    }
}
