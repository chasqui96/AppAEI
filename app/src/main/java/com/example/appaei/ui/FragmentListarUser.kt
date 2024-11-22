package com.example.appaei.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appaei.R
import com.example.appaei.databinding.FragmentListarUserBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

class FragmentListarUser : Fragment(R.layout.fragment_listar_user) {

    private lateinit var binding: FragmentListarUserBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var userAdapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListarUserBinding.inflate(inflater, container, false)

        db = FirebaseFirestore.getInstance()

        // Inicializa el RecyclerView
        userAdapter = UserAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = userAdapter

        // Cargar usuarios desde Firestore
        loadUsers()

        return binding.root
    }

    private fun loadUsers() {
        db.collection("users") // Asegúrate de usar el nombre correcto de tu colección
            .get()
            .addOnSuccessListener { result ->
                val users = mutableListOf<User>()
                for (document in result) {
                    val user = document.toObject(User::class.java)
                    users.add(user)
                }
                userAdapter.submitList(users)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error al cargar los usuarios", Toast.LENGTH_SHORT).show()
            }
    }
}
