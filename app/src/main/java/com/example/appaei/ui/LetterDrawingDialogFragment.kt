package com.example.appaei.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.example.appaei.R

class LetterDrawingDialogFragment : DialogFragment() {

    private lateinit var drawingView: DrawingView

    // Clave para el argumento de la letra
    companion object {
        const val ARG_LETTER = "arg_letter"

        fun newInstance(letterResId: Int): LetterDrawingDialogFragment {
            val fragment = LetterDrawingDialogFragment()
            val args = Bundle()
            args.putInt(ARG_LETTER, letterResId) // Pasa el recurso de la letra como argumento
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // Opcional: quitar título
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout del diálogo
        val view = inflater.inflate(R.layout.fragment_letter_drawing_dialog, container, false)

        // Inicializar la vista de dibujo
        drawingView = view.findViewById(R.id.drawing_view)

        // Obtener el recurso de la letra desde los argumentos
        val letterResId = arguments?.getInt(ARG_LETTER) ?: R.drawable.bichito2_1 // Valor por defecto

        // Cargar la imagen de la letra
        drawingView.setLetterResource(letterResId)

        // Botón para limpiar el dibujo
        val clearButton: Button = view.findViewById(R.id.clear_button)
        clearButton.setOnClickListener {
            drawingView.clearDrawing() // Limpiar el dibujo
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
        // Ajustar el tamaño del diálogo
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}
