package com.example.appaei.ui

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.appaei.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class FragmentFeedBackAnimales : Fragment() {

    private val animales = listOf(
        Animal("Perro", R.drawable.animal_perro, R.raw.sonidoperro),
        Animal("Gato", R.drawable.animal_gato, R.raw.sonidogato),
        Animal("Gallina", R.drawable.animal_gallina, R.raw.sonidogallina),
        Animal("Vaca", R.drawable.animal_vaca, R.raw.sonidovaca),
        Animal("Cerdo", R.drawable.animal_chancho_dos, R.raw.sonidochancho)
    )

    private var sonidoActual: MediaPlayer? = null
    private lateinit var animalSeleccionado: Animal
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private var correctSound: MediaPlayer? = null
    private var incorrectSound: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_freedback_animales, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imagenesAnimales = view.findViewById<LinearLayout>(R.id.imagenesAnimales)
        val cuadrosSoltar = view.findViewById<LinearLayout>(R.id.cuadrosSoltar)
        val btnSonido = view.findViewById<Button>(R.id.btnSonido)
        val textoInstruccion = view.findViewById<TextView>(R.id.textoInstruccion)

        // Inicializar sonidos
        correctSound = MediaPlayer.create(requireContext(), R.raw.correcto)
        incorrectSound = MediaPlayer.create(requireContext(), R.raw.incorrecto)

        // Agregar imágenes de animales dinámicamente
        animales.forEach { animal ->
            val imageView = ImageView(requireContext()).apply {
                setImageResource(animal.imagenRes)
                layoutParams = LinearLayout.LayoutParams(200, 200).apply {
                    setMargins(16, 0, 16, 0)
                }
                setOnLongClickListener {
                    animalSeleccionado = animal
                    val dragData = View.DragShadowBuilder(this)
                    startDragAndDrop(null, dragData, this, 0)
                    true
                }
            }
            imagenesAnimales.addView(imageView)

            // Crear cuadro de destino dinámico
            val cuadro = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(200, 200).apply {
                    setMargins(16, 0, 16, 0)
                }
                setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
                tag = animal.nombre
                setOnDragListener { _, event ->
                    if (event.action == DragEvent.ACTION_DROP) {
                        if (animalSeleccionado.nombre == this.tag) {
                            this.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_light))
                            Toast.makeText(requireContext(), "¡Correcto! Es un ${animal.nombre}", Toast.LENGTH_SHORT).show()
                            correctSound?.start() // Reproducir sonido correcto
                            // Animación de éxito
                            this.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.bounce))

                            // Registrar fallo correcto en Firebase
                            registrarFalloFirebase(animalSeleccionado.nombre, true)

                        } else {
                            this.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_light))
                            Toast.makeText(requireContext(), "¡Intenta de nuevo!", Toast.LENGTH_SHORT).show()
                            incorrectSound?.start() // Reproducir sonido incorrecto

                            // Registrar fallo incorrecto en Firebase
                            registrarFalloFirebase(animalSeleccionado.nombre, false)
                        }
                    }
                    true
                }
            }
            cuadrosSoltar.addView(cuadro)
        }

        // Configurar botón para reproducir sonido
        btnSonido.setOnClickListener {
            sonidoActual?.stop()
            sonidoActual = MediaPlayer.create(requireContext(), animalSeleccionado.sonidoRes)
            sonidoActual?.start()
        }
    }

    // Función para registrar fallos en Firebase
    private fun registrarFalloFirebase(animalNombre: String, esCorrecto: Boolean) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val pantalla = "pantalla_retroalimentacion_animales" // Nombre de la pantalla actual

            // Obtener la referencia a Firestore
            val db = FirebaseFirestore.getInstance()

            // Obtener la referencia al documento del usuario
            val userFallosRef = db.collection("fallos")
                .document(userId)
                .collection(pantalla)
                .document(animalNombre)

            // Obtener el conteo actual de fallos
            userFallosRef.get().addOnSuccessListener { documentSnapshot ->
                val conteoActual = documentSnapshot.getLong("fallos")?.toInt() ?: 0
                val nuevoConteo = if (esCorrecto) conteoActual else conteoActual + 1

                // Actualizar el conteo de fallos
                userFallosRef.set(mapOf("fallos" to nuevoConteo))
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Fallo registrado exitosamente", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Error al guardar el fallo: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }.addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al obtener los datos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Usuario no logueado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sonidoActual?.release()
        correctSound?.release()
        incorrectSound?.release()
    }
}
