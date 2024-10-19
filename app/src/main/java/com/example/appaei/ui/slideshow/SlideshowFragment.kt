package com.example.appaei.ui.slideshow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.appaei.databinding.FragmentSlideshowBinding
import com.example.appaei.ui.FragmenMenuRetroAlimentacion
import com.example.appaei.ui.FragmentSentidos
import com.example.appaei.ui.ImageActivity

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

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
