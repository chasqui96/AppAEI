package com.example.appaei.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log // Importa la clase Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.VideoView
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.example.appaei.R

class VideoDialogFragment : DialogFragment() {

    companion object {
        private const val ARG_VIDEO_URI = "video_uri"
        private const val ARG_VIDEO_URL = "video_url"

        fun newInstanceLocal(videoUri: Uri): VideoDialogFragment {
            val fragment = VideoDialogFragment()
            val args = Bundle()
            args.putString(ARG_VIDEO_URI, videoUri.toString())
            fragment.arguments = args
            return fragment
        }

        fun newInstanceYouTube(videoUrl: String): VideoDialogFragment {
            val fragment = VideoDialogFragment()
            val args = Bundle()
            args.putString(ARG_VIDEO_URL, videoUrl)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_video_dialog, container, false)

        val videoView: VideoView = view.findViewById(R.id.video_view)
        val webView: WebView = view.findViewById(R.id.webview)
        val playButton: Button = view.findViewById(R.id.play_button)

        val videoUriString = arguments?.getString(ARG_VIDEO_URI)
        val videoUrlString = arguments?.getString(ARG_VIDEO_URL)

        if (videoUriString != null) {
            // Configurar el VideoView para video local
            videoView.visibility = View.VISIBLE
            webView.visibility = View.GONE
            val videoUri = Uri.parse(videoUriString)
            videoView.setVideoURI(videoUri)


            playButton.setOnClickListener {
                if (videoView.isPlaying) {
                    videoView.pause()
                    playButton.text = "Reproducir Video"
                } else {
                    videoView.start()
                    playButton.text = "Pausar Video"
                }
            }
        } else if (videoUrlString != null) {
            // Aquí puedes agregar un log
            Log.d("VideoDialogFragment", "Configurando WebView para el video de YouTube: $videoUrlString")

            // Configurar el botón para abrir el video de YouTube
            playButton.setOnClickListener {
                val videoUrl = "https://www.youtube.com/watch?v=$videoUrlString" // Asegúrate de usar videoUrlString
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
                startActivity(intent)
            }
        }
        // Botón para cerrar el diálogo
        val closeButton: Button = view.findViewById(R.id.close_button)
        closeButton.setOnClickListener {
            dismiss() // Cerrar el diálogo
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}
