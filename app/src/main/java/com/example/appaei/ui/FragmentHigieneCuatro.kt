package com.example.appaei.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.appaei.R

class FragmentHigieneCuatro : Fragment() {

    private var mediaPlayer1: MediaPlayer? = null
    private var mediaPlayer2: MediaPlayer? = null
    private var isPlaying1 = false  // Estado de reproducción para el primer audio
    private var isPlaying2 = false  // Estado de reproducción para el segundo audio

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla el layout del fragmento
        val view = inflater.inflate(R.layout.fragment_higiene_cuatro, container, false)

        // Encuentra el botón por su ID para el primer audio
        val playButton1: Button = view.findViewById(R.id.image_start_button_uno)
        // Inicializa el MediaPlayer con el archivo de sonido
        mediaPlayer1 = MediaPlayer.create(context, R.raw.ropalimpia)

        // Configura el evento onClick del botón para el primer audio
        playButton1.setOnClickListener {
            if (isPlaying1) {
                // Si el sonido está reproduciéndose, deténlo
                mediaPlayer1?.pause()  // Pausa el sonido
                mediaPlayer1?.seekTo(0)  // Reinicia el sonido desde el principio
                playButton1.text = "Reproducir"  // Cambia el texto del botón
            } else {
                // Si el sonido no está reproduciéndose, inícialo
                mediaPlayer1?.start()  // Reproduce el sonido
                playButton1.text = "Detener"  // Cambia el texto del botón
            }
            isPlaying1 = !isPlaying1  // Cambia el estado
        }

        // Encuentra el botón por su ID para el segundo audio
        val playButton2: Button = view.findViewById(R.id.image_start_button_dos)
        // Inicializa el MediaPlayer con el archivo de sonido
        mediaPlayer2 = MediaPlayer.create(context, R.raw.peinarse)

        // Configura el evento onClick del botón para el segundo audio
        playButton2.setOnClickListener {
            if (isPlaying2) {
                // Si el sonido está reproduciéndose, deténlo
                mediaPlayer2?.pause()  // Pausa el sonido
                mediaPlayer2?.seekTo(0)  // Reinicia el sonido desde el principio
                playButton2.text = "Reproducir"  // Cambia el texto del botón
            } else {
                // Si el sonido no está reproduciéndose, inícialo
                mediaPlayer2?.start()  // Reproduce el sonido
                playButton2.text = "Detener"  // Cambia el texto del botón
            }
            isPlaying2 = !isPlaying2  // Cambia el estado
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Libera los MediaPlayer cuando el fragmento se destruye
        mediaPlayer1?.release()
        mediaPlayer1 = null
        mediaPlayer2?.release()
        mediaPlayer2 = null
    }
}
