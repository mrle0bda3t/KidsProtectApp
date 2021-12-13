package com.loan555.kisdapplication2.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.loan555.kisdapplication2.ui.fragment.DashboardFragment
import com.loan555.kisdapplication2.ui.fragment.HomeFragment
import com.loan555.kisdapplication2.ui.fragment.NotificationsFragment

class ViewPagerMainAdapter(fa: FragmentManager, private val listener : HomeFragment.LoadDataResult) :
    FragmentPagerAdapter(fa, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int = 3

    override fun getItem(position: Int): Fragment {
        return when (position) {
            1 -> {
                DashboardFragment()
            }
            2 -> {
                NotificationsFragment()
            }
            else -> {
                HomeFragment(listener)
            }
        }
    }
}