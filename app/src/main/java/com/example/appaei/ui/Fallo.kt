package com.example.appaei.ui

data class Fallo(
    val animalNombre: String = "",
    val intentos: Int = 0, // La cantidad de intentos fallidos
    val esCorrecto: Boolean = false // Si el fallo fue correcto o no
)