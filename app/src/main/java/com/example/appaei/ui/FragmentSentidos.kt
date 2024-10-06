package com.example.appaei.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.appaei.R

class FragmentSentidos: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_higiene)

        // Crea una lista de fragmentos para cada vocal
        val fragments = listOf(
            FragmentSentidoPrimero(),
            FragmentVista(),
            FragmentOlfato(),
            FragmentOido(),
            FragmentTacto(),
            FragmentGusto()
        )

        // Configura el ViewPager con el adaptador
        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        val adapter = VocalesPagerAdapter(supportFragmentManager, fragments)
        viewPager.adapter = adapter
    }
}