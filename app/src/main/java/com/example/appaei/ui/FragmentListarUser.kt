package com.example.appaei.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appaei.R
import com.example.appaei.databinding.FragmentListarUserBinding
import com.google.firebase.auth.FirebaseAuth
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
        val currentUser = FirebaseAuth.getInstance().currentUser

        val userId = currentUser?.uid
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Log.d("LoadUsers", "No se encontraron usuarios.")
                } else {
                    val users = mutableListOf<User>()
                    for (document in result) {
                        val user = document.toObject(User::class.java)
                        users.add(user)
                    }
                    userAdapter.submitList(users)
                    Log.d("LoadUsers", "Usuarios cargados correctamente.")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("LoadUsersError", "Error al cargar los usuarios", exception)
                Toast.makeText(requireContext(), "Error al cargar los usuarios", Toast.LENGTH_SHORT).show()
            }

    }

}
