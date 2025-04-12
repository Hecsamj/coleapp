package com.example.coleapp

data class AlumnoAsistencia(
    val correo: String,
    val nombre: String,
    val nivel: String,
    val grado: String,
    var asistio: Boolean = false
)
