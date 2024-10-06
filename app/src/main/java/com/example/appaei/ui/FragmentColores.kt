package com.example.appaei.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.appaei.R

class FragmentColores : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_colores)

        // Crea una lista de fragmentos para cada vocal
        val fragments = listOf(
            FragmentColorGris(),
            FragmentColorVerde(),
            FragmentColorRojo(),
            FragmentColorAzul(),
            FragmentColorAmarillo()

        )
        // Configura el ViewPager con el adaptador
        val viewPager = findViewById<ViewPager>(R.id.viewPagerColores)
        val adapter = ColoresPagerAdapter(supportFragmentManager, fragments)
        viewPager.adapter = adapter
    }
}