package com.example.appaei.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class ColoresPagerAdapter(fm: FragmentManager, private val fragments: List<Fragment>) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "GRIS"
            1 -> "VERDE"
            2 -> "ROJO"
            3 -> "AZUL"
            4 -> "AMARILLO"
            else -> ""
        }
    }
}
