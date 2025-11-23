package com.example.phishingshield.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.phishingshield.R

class ListFragment : Fragment() {
    private val vm: LogsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val list = view.findViewById<RecyclerView>(R.id.list)
        val empty = view.findViewById<TextView>(R.id.empty)
        val adapter = ThreatAdapter()
        list.layoutManager = LinearLayoutManager(requireContext())
        list.adapter = adapter

        val which = arguments?.getString(ARG_KIND) ?: "Safe"
        val obs = when (which) {
            "Smishing" -> vm.smishing
            "Spam"     -> vm.spam
            "Safe"     -> vm.safe
            else       -> vm.safe
        }
        val observer = Observer { items: List<com.example.phishingshield.data.Threat> ->
            adapter.submitList(items)
            empty.visibility = if (items.isNullOrEmpty()) View.VISIBLE else View.GONE
        }
        obs.observe(viewLifecycleOwner, observer)
    }

    companion object {
        private const val ARG_KIND = "kind"
        fun newInstance(kind: String) = ListFragment().apply {
            arguments = Bundle().apply { putString(ARG_KIND, kind) }
        }
    }
}
