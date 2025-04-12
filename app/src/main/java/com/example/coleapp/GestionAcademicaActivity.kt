package com.example.coleapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appcole.OpcionGestion

class GestionAcademicaActivity : AppCompatActivity() {

    private lateinit var gridView: GridView
    private lateinit var opcionesAdapter: OpcionGestionAdapter

    private val opciones = listOf(
        OpcionGestion("Registro de calificaciones", R.drawable.ic_calificaciones),
        OpcionGestion("Registro de asistencias", R.drawable.ic_asistencia),
        OpcionGestion("Cursos y horarios", R.drawable.ic_horarios),
        OpcionGestion("Ver asistencias", R.drawable.ic_ver_asistencias) // <- NUEVO
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_academica)

        gridView = findViewById(R.id.gridOpciones)
        opcionesAdapter = OpcionGestionAdapter(this, opciones)
        gridView.adapter = opcionesAdapter

        gridView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> startActivity(Intent(this, RegistroCalificacionActivity::class.java))
                1 -> startActivity(Intent(this, RegistroAsistenciaActivity::class.java))
                2 -> startActivity(Intent(this, CursosHorariosActivity::class.java))
                3 -> startActivity(Intent(this, VerAsistenciasActivity::class.java)) // <- NUEVO
                else -> Toast.makeText(this, "Opción en desarrollo", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
