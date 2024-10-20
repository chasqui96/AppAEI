package com.example.appaei.ui

import android.os.Bundle
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.appaei.R
import com.example.appaei.databinding.ActivityMenuBinding
import android.content.Intent
import android.widget.LinearLayout

class MenuActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setSupportActionBar(binding.appBarMenu.toolbar)


        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_menu)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        //setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        //supportActionBar?.setHomeAsUpIndicator(R.drawable.vector)// Usa tu nuevo ícono aquí
        val irVocales = findViewById<LinearLayout>(R.id.Linear6)
        irVocales.setOnClickListener {
            // Navegar a la actividad de carrusel
            val intent = Intent(this, FramentVocales::class.java)
            startActivity(intent)
        }
        val irHigiene = findViewById<LinearLayout>(R.id.Linear9)
        irHigiene.setOnClickListener {
            // Navegar a la actividad de carrusel
            val intent = Intent(this, FragmentHigiene::class.java)
            startActivity(intent)
        }
        val irColores = findViewById<LinearLayout>(R.id.Linear5)
        irColores.setOnClickListener {
            // Navegar a la actividad de carrusel
            val intent = Intent(this, FragmentColores::class.java)
            startActivity(intent)
        }
        val irSentidos = findViewById<LinearLayout>(R.id.Linear11)
        irSentidos.setOnClickListener {
            // Navegar a la actividad de carrusel
            val intent = Intent(this, FragmentSentidos::class.java)
            startActivity(intent)
        }
        val irEmociones = findViewById<LinearLayout>(R.id.Linear12)
        irEmociones.setOnClickListener {
            // Navegar a la actividad de carrusel
            val intent = Intent(this, FragmentEmociones::class.java)
            startActivity(intent) 
        }
        val irAnimales= findViewById<LinearLayout>(R.id.Linear8)
        irAnimales.setOnClickListener {
            // Navegar a la actividad de carrusel
            val intent = Intent(this, FragmentAnimales::class.java)
            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_menu)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}