package com.example.appaei.ui.slideshow

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.appaei.databinding.FragmentSlideshowBinding
import com.example.appaei.ui.FragmenMenuRetroAlimentacion
import com.example.appaei.ui.FragmentSentidos
import com.example.appaei.ui.ImageActivity
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class SlideShowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null

    // Esta propiedad solo es válida entre onCreateView y onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Configura el botón para registrar un log al hacer clic
        binding.imageRectangleGames.setOnClickListener {
            val intent = Intent(activity, FragmenMenuRetroAlimentacion::class.java)
            startActivity(intent)
        }


        binding.imageProyectar.setOnClickListener {
            val intent = Intent(activity, ImageActivity::class.java)
            startActivity(intent)
        }
        // Configura los botones
        binding.imprimirPdf.setOnClickListener {
            // Abrir el PDF desde assets
            openPDFFromAssets("ayuda_interactiva.pdf")
        }


        return root
    }
    private fun openPDFFromAssets(assetFileName: String) {
        try {
            // Copiar el archivo desde assets a una ubicación accesible
            val inputStream: InputStream = requireContext().assets.open(assetFileName)
            val outputFile = File(requireContext().getExternalFilesDir(null), assetFileName)
            val outputStream = FileOutputStream(outputFile)

            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()

            // Usar FileProvider para obtener una URI segura
            val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", outputFile)

            // Crear un Intent para abrir el archivo PDF
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

            // Inicia la actividad para abrir el PDF
            startActivity(Intent.createChooser(intent, "Abrir PDF con"))

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error al abrir el PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
