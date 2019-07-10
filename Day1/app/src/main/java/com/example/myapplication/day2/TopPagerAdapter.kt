package com.example.myapplication.day2

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class TopPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
    var list: ArrayList<Fragment> = ArrayList()

    override fun getItem(position: Int): Fragment {
        return list[position]
    }

    override fun getCount() = 2

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "HOT"
            1 -> "ENERGY"
            else -> "---"
        }
    }

    fun addFragment(fragment: Fragment) {
        list.add(fragment)
    }
}