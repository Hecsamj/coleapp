package com.example.coleapp

import com.google.firebase.Timestamp


data class AlumnoAsistencia(
    val correo: String,
    val nombre: String,
    val nivel: String,
    val grado: String,
    var asistio: Boolean = false,
    val fecha: Timestamp? = null
)

