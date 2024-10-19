package com.example.appaei.ui

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager.widget.ViewPager
import com.example.appaei.R
import com.example.appaei.databinding.ActivityMenuBinding
import com.google.android.material.navigation.NavigationView
class FragmenMenuRetroAlimentacion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_retroalimentacion)

        val buttonVocales = findViewById<LinearLayout>(R.id.Linear6)
        buttonVocales.setOnClickListener {

            setButtonsEnabled(false)
            // Crear una instancia del Fragment
            val fragmentvocales= FragmentColorGris()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragmentvocales) // Cambia el ID al contenedor correcto
                .addToBackStack(null) // Permite volver al fragmento anterior
                .commit() // Ejecuta la transacción
        }

        val irHigiene = findViewById<LinearLayout>(R.id.Linear9)
        irHigiene.setOnClickListener {
            // Navegar a la actividad de carrusel
            setButtonsEnabled(false)
            // Crear una instancia del Fragment
            val fragmenthigiene= FragmentHigieneUno()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragmenthigiene) // Cambia el ID al contenedor correcto
                .addToBackStack(null) // Permite volver al fragmento anterior
                .commit() // Ejecuta la transacción
        }
        val irColores = findViewById<LinearLayout>(R.id.Linear5)
        irColores.setOnClickListener {
            setButtonsEnabled(false)
            val fragmentColorGris = FragmentColorGris()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragmentColorGris) // Cambia el ID al contenedor correcto
                .addToBackStack(null) // Permite volver al fragmento anterior
                .commit() // Ejecuta la transacción
        }
        val irSentidos = findViewById<LinearLayout>(R.id.Linear11)
        irSentidos.setOnClickListener {
            setButtonsEnabled(false)
            val fragmentsentidos = FragmentSentidoPrimero()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragmentsentidos) // Cambia el ID al contenedor correcto
                .addToBackStack(null) // Permite volver al fragmento anterior
                .commit() // Ejecuta la transacción
        }
        val irEmociones = findViewById<LinearLayout>(R.id.Linear12)
        irEmociones.setOnClickListener {


            val dialog = LetterDrawingDialogFragment.newInstance(R.drawable.lasemocionesdibujarr) // Cambia por la imagen de la letra A
            dialog.show(supportFragmentManager, "LetterDrawingDialog") // Usa supportFragmentManager directamente
        }

        val irAnimales= findViewById<LinearLayout>(R.id.Linear8)
        irAnimales.setOnClickListener {
            setButtonsEnabled(false)
            val fragmentanimales = FragmentAnimalPrimero()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragmentanimales) // Cambia el ID al contenedor correcto
                .addToBackStack(null) // Permite volver al fragmento anterior
                .commit() // Ejecuta la transacción
        }
    }
    private fun setButtonsEnabled(enabled: Boolean) {
        val buttonVocales = findViewById<LinearLayout>(R.id.Linear6)
        val irHigiene = findViewById<LinearLayout>(R.id.Linear9)
        val irColores = findViewById<LinearLayout>(R.id.Linear5)
        val irSentidos = findViewById<LinearLayout>(R.id.Linear11)
        val irEmociones = findViewById<LinearLayout>(R.id.Linear12)
        val irAnimales = findViewById<LinearLayout>(R.id.Linear8)

        buttonVocales.isEnabled = enabled
        irHigiene.isEnabled = enabled
        irColores.isEnabled = enabled
        irSentidos.isEnabled = enabled
        irEmociones.isEnabled = enabled
        irAnimales.isEnabled = enabled
    }
    override fun onResume() {
        super.onResume()
        // Habilitar los botones cuando la actividad vuelve a estar en primer plano
        setButtonsEnabled(true)
    }
    override fun onBackPressed() {
        super.onBackPressed()
        // Habilitar los botones al volver atrás
        setButtonsEnabled(true)
    }

}
