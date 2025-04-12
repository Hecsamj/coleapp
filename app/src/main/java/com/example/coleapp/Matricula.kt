package com.example.coleapp.models

data class Matricula(
    val id: String = "",
    val nombresCompletos: String = "",  // Aquí guardamos el nombre completo
    val apellidos: String = "",  // Apellidos completos
    val fechaNacimiento: String = "",  // Fecha de nacimiento
    val grado: String = "",  // Ejemplo: "1ro de primaria" o "2do de secundaria"
    val foto: String = "",  // URL de la foto
    val colegioProcedencia: String? = null  // Opcional
)
