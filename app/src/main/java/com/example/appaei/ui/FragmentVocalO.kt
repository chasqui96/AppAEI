package com.example.appaei.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

import com.example.appaei.R

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentVocalO.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentVocalO : Fragment() {
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false  // Variable para rastrear si el sonido está reproduciéndose
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_vocal_o, container, false)
        val playButton: Button = view.findViewById(R.id.image_start_button)

        // Inicializa el MediaPlayer con el archivo de sonido
        mediaPlayer = MediaPlayer.create(context, R.raw.letra_o)

        // Configura el evento onClick del botón
        playButton.setOnClickListener {
            if (isPlaying) {
                // Si el sonido está reproduciéndose, deténlo
                mediaPlayer?.pause()  // Pausa el sonido
                mediaPlayer?.seekTo(0)  // Reinicia el sonido desde el principio si se reproduce de nuevo
                playButton.text = "  Reproducir"  // Cambia el texto del botón
            } else {
                // Si el sonido no está reproduciéndose, inícialo
                mediaPlayer?.start()  // Reproduce el sonido
                playButton.text = "  Detener"  // Cambia el texto del botón
            }
            isPlaying = !isPlaying  // Cambia el estado
        }
        // Botón para abrir el diálogo de dibujo para la letra E
        val openDialogButtonE: Button = view.findViewById(R.id.open_dialog_button_o)
        openDialogButtonE.setOnClickListener {
            val dialog = LetterDrawingDialogFragment.newInstance(R.drawable.letra_o) // Cambia por la imagen de la letra E
            dialog.show(requireActivity().supportFragmentManager, "LetterDrawingDialog")
        }
        return view
    }
}