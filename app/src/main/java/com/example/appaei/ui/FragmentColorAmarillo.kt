package com.example.appaei.ui

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.appaei.R

class FragmentColorAmarillo:Fragment() {

    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false  // Variable para rastrear si el sonido está reproduciéndose

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla el layout del fragmento
        val view = inflater.inflate(R.layout.fragment_color_amarillo, container, false)

        // Encuentra el botón por su ID
        val playButton: Button = view.findViewById(R.id.image_start_button)

        // Inicializa el MediaPlayer con el archivo de sonido
        mediaPlayer = MediaPlayer.create(context, R.raw.color_amarrillo_principal)

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
        val openLocalButton: Button = view.findViewById(R.id.open_dialog_button_color_verde)
        openLocalButton.setOnClickListener {
            val videoUri =
                Uri.parse("android.resource://" + requireContext().packageName + "/" + R.raw.color_rojo)
            showLocalVideoDialog(videoUri)
        }


        return view
    }

    private fun showLocalVideoDialog(videoUri: Uri) {
        val dialog = VideoDialogFragment.newInstanceLocal(videoUri)
        dialog.show(childFragmentManager, "VideoDialogFragment")
    }

    private fun showYouTubeVideoDialog(videoUrl: String) {
        val dialog = VideoDialogFragment.newInstanceYouTube(videoUrl)
        dialog.show(childFragmentManager, "VideoDialogFragment")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Libera el MediaPlayer cuando el fragmento se destruye
        mediaPlayer?.release()
        mediaPlayer = null
    }
}