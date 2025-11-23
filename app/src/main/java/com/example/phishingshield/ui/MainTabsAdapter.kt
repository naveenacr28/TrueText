package com.example.phishingshield.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainTabsAdapter(activity: FragmentActivity): FragmentStateAdapter(activity) {
    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> ListFragment.newInstance("Safe")
            1 -> ListFragment.newInstance("Smishing")
            2 -> ListFragment.newInstance("Spam")
            else -> ListFragment.newInstance("Safe")
        }
    }
}
