package com.example.appaei.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.appaei.R

class ConfigurarIpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el diseño del fragmento
        return inflater.inflate(R.layout.fragment_configurar_ip, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editTextIp: EditText = view.findViewById(R.id.editTextIp)
        val saveButton: Button = view.findViewById(R.id.saveButton)

        // Obtener la IP guardada y mostrarla en el campo de texto
        val sharedPreferences = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val savedIp = sharedPreferences.getString("server_ip", "")
        editTextIp.setText(savedIp)

        saveButton.setOnClickListener {
            val ip = editTextIp.text.toString().trim()

            if (ip.isNotEmpty()) {
                // Guardar la nueva IP
                sharedPreferences.edit().putString("server_ip", ip).apply()
                Toast.makeText(requireContext(), "Dirección IP guardada correctamente", Toast.LENGTH_SHORT).show()
                editTextIp.setText("")
            } else {
                Toast.makeText(requireContext(), "Por favor, introduce una dirección IP válida", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
