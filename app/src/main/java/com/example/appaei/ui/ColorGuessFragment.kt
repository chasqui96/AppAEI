package com.example.appaei.ui

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.appaei.R
import java.util.*

class ColorGuessFragment : Fragment(R.layout.fragment_feedback_colores) {

    private val colors = listOf(
        ColorItem("Rojo", Color.RED),
        ColorItem("Verde", Color.GREEN),
        ColorItem("Azul", Color.BLUE),
        ColorItem("Amarillo", Color.YELLOW)
    )
    private var currentColor: ColorItem? = null
    private val handler = Handler()

    data class ColorItem(val name: String, val color: Int)

    private var correctSound: MediaPlayer? = null
    private var incorrectSound: MediaPlayer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar sonidos
        correctSound = MediaPlayer.create(requireContext(), R.raw.correcto)
        incorrectSound = MediaPlayer.create(requireContext(), R.raw.incorrecto)

        // Referenciar botones
        val btnColor1: Button = view.findViewById(R.id.btnColor1)
        val btnColor2: Button = view.findViewById(R.id.btnColor2)
        val btnColor3: Button = view.findViewById(R.id.btnColor3)
        val btnColor4: Button = view.findViewById(R.id.btnColor4)

        // Configurar botones
        val buttons = listOf(btnColor1, btnColor2, btnColor3, btnColor4)

        startColorCycle(buttons)

        // Listener para cada botón
        buttons.forEach { button ->
            button.setOnClickListener {
                val selectedColorName = button.text.toString()
                if (selectedColorName.equals(currentColor?.name, ignoreCase = true)) {
                    showFeedback("¡Correcto!", Color.GREEN)
                    correctSound?.start() // Reproducir sonido correcto
                } else {
                    showFeedback("¡Incorrecto! Intenta nuevamente.", Color.RED)
                    incorrectSound?.start() // Reproducir sonido incorrecto
                }
            }
        }
    }

    private fun startColorCycle(buttons: List<Button>) {
        handler.postDelayed(object : Runnable {
            override fun run() {
                // Elegir un color aleatorio
                currentColor = colors.random()

                // Cambiar el fondo del CardView al color seleccionado
                view?.findViewById<androidx.cardview.widget.CardView>(R.id.colorCard)
                    ?.setCardBackgroundColor(currentColor?.color ?: Color.WHITE)

                // Configurar los botones con opciones aleatorias
                val shuffledColors = colors.shuffled()
                buttons.forEachIndexed { index, button ->
                    button.text = shuffledColors.getOrNull(index)?.name ?: "Otro"
                }

                // Repetir cada 10 segundos
                handler.postDelayed(this, 3000)
            }
        }, 0)
    }

    private fun showFeedback(message: String, textColor: Int) {
        val feedbackMessage = view?.findViewById<android.widget.TextView>(R.id.feedbackMessage)
        feedbackMessage?.text = message
        feedbackMessage?.setTextColor(textColor)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Liberar recursos de MediaPlayer
        correctSound?.release()
        incorrectSound?.release()
    }
}
