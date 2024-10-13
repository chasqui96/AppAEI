package com.example.appaei.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.appaei.R

class FragmentAnimales: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_animales)

        // Crea una lista de fragmentos para cada vocal
        val fragments = listOf(
            FragmentAnimalPerro(),
            FragmentAnimalGato(),
            FragmentAnimalVaca(),
            FragmentAnimalGallo()
        )

        // Configura el ViewPager con el adaptador
        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        val adapter = VocalesPagerAdapter(supportFragmentManager, fragments)
        viewPager.adapter = adapter
    }
}