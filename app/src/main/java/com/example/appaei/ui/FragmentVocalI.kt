package com.example.appaei.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

import com.example.appaei.R

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentVocalI.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentVocalI : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_vocal_i, container, false)
        // Botón para abrir el diálogo de dibujo para la letra E
        val openDialogButtonE: Button = view.findViewById(R.id.open_dialog_button_i)
        openDialogButtonE.setOnClickListener {
            val dialog = LetterDrawingDialogFragment.newInstance(R.drawable.imagen_letra_i) // Cambia por la imagen de la letra E
            dialog.show(requireActivity().supportFragmentManager, "LetterDrawingDialog")
        }
        return view
    }

}