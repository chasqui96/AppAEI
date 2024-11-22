package com.example.appaei.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appaei.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicializa FirebaseAuth y Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Vincula los elementos del diseño
        val registerButton = findViewById<Button>(R.id.registerButton)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)

        // Configura el botón de registro
        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Registrar usuario en Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Si el registro es exitoso, guardar los datos en Firestore
                        val user = hashMapOf(
                            "email" to email,
                            "password" to password // NO almacenar la contraseña en texto claro en producción
                        )

                        // Obtener el ID del usuario recién creado
                        val userId = auth.currentUser?.uid ?: return@addOnCompleteListener

                        // Guardar los detalles del usuario en Firestore
                        db.collection("users")
                            .document(userId)
                            .set(user)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Usuario guardado en Firestore", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error al guardar usuario: ${e.message}", Toast.LENGTH_SHORT).show()
                            }

                        // Redirigir al LoginActivity
                        Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, LoginpageActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
