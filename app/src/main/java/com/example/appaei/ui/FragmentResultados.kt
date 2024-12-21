package com.example.appaei.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appaei.R
import com.example.appaei.databinding.FragmentResultadosBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

class FragmentResultados : Fragment(R.layout.fragment_resultados) {

    private lateinit var binding: FragmentResultadosBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var falloAdapter: FalloAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentResultadosBinding.inflate(inflater, container, false)

        // Asegúrate de que el RecyclerView está correctamente inicializado
        falloAdapter = FalloAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = falloAdapter

        // Cargar los datos de los fallos
        loadFallos()
        FirebaseFirestore.setLoggingEnabled(true)

        return binding.root
    }


    private fun loadFallos() {
        try {
            db = FirebaseFirestore.getInstance()
            db.collection("fallos")
                .get()
                .addOnSuccessListener { result ->
                    // Procesa los documentos recuperados
                    val fallos = mutableListOf<Fallo>()
                    for (document in result) {
                        val fallo = document.toObject(Fallo::class.java)
                        fallos.add(fallo)
                    }
                    falloAdapter.submitList(fallos)
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Error en la consulta: ${exception.message}", Toast.LENGTH_LONG).show()
                }
        } catch (e: Exception) {
            // Esto atrapará cualquier error fuera de los listeners
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error inesperado: ${e.message}", Toast.LENGTH_LONG).show()
        }



    }
}
