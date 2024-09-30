package com.example.appaei.ui

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.VideoView
import androidx.fragment.app.DialogFragment
import com.example.appaei.R

class VideoDialogFragment : DialogFragment() {

    private var isPlaying = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla el layout del diálogo
        val view = inflater.inflate(R.layout.fragment_video_dialog, container, false)

        // Encuentra el VideoView y el botón
        val videoView: VideoView = view.findViewById(R.id.video_view)
        val playButton: Button = view.findViewById(R.id.play_button)

        // Configura el video desde una URL o archivo local
        val videoUri = Uri.parse("android.resource://" + requireContext().packageName + "/" + R.raw.color_verde_ok) // Archivo en res/raw

        videoView.setVideoURI(videoUri)

        // Configura el botón para reproducir/pausar el video
        playButton.setOnClickListener {
            if (isPlaying) {
                videoView.pause()
                playButton.text = "Reproducir Video"
            } else {
                videoView.start()
                playButton.text = "Pausar Video"
            }
            isPlaying = !isPlaying
        }

        // Listener para cuando el video termine
        videoView.setOnCompletionListener(MediaPlayer.OnCompletionListener {
            playButton.text = "Reproducir Video"
            isPlaying = false
        })

        return view
    }
    override fun onStart() {
        super.onStart()
        // Ajustar el tamaño del diálogo
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}
