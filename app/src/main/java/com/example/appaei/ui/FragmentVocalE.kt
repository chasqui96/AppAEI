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
 * Use the [FragmentVocalE.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentVocalE : Fragment() {
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false  // Variable para rastrear si el sonido está reproduciéndose

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_vocal_e, container, false)
        // Botón para abrir el diálogo de dibujo para la letra E
        val openDialogButtonE: Button = view.findViewById(R.id.open_dialog_button_e)
        openDialogButtonE.setOnClickListener {
            val dialog = LetterDrawingDialogFragment.newInstance(R.drawable.imagen_letra_e) // Cambia por la imagen de la letra E
            dialog.show(requireActivity().supportFragmentManager, "LetterDrawingDialog")
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Libera el MediaPlayer cuando el fragmento se destruye
        mediaPlayer?.release()
        mediaPlayer = null
    }

}