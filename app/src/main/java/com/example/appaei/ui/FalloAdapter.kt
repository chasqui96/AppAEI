package com.example.appaei.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appaei.databinding.ItemResultadoBinding

class FalloAdapter : RecyclerView.Adapter<FalloAdapter.FalloViewHolder>() {

    private var fallos: List<Fallo> = listOf()

    // Método para actualizar la lista de fallos
    fun submitList(list: List<Fallo>) {
        fallos = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FalloViewHolder {
        val binding = ItemResultadoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FalloViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FalloViewHolder, position: Int) {
        val fallo = fallos[position]
        holder.bind(fallo)
    }

    override fun getItemCount(): Int = fallos.size

    inner class FalloViewHolder(private val binding: ItemResultadoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(fallo: Fallo) {
            binding.animalNombre.text = fallo.animalNombre
            binding.intentos.text = "Intentos fallidos: ${fallo.intentos}"
            binding.resultado.text = if (fallo.esCorrecto) "Correcto" else "Incorrecto"
        }
    }
}
