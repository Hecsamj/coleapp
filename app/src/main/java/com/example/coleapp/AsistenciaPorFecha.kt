package com.example.coleapp

data class AsistenciaPorFecha(
    val fecha: String = "",
    val alumnos: List<AsistenciaAlumno> = emptyList()
)
