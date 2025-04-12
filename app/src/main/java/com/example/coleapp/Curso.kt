package com.example.coleapp

data class Curso(
    val curso: String = "",
    val horarios: List<Horario> = emptyList()
)

data class Horario(
    val horaInicio: String = "",
    val horaFin: String = "",
    val dias: List<String> = emptyList()
)
