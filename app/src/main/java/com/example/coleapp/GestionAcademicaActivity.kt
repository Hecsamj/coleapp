package com.example.coleapp

import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

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

        // Inicializar vistas
        gridView = findViewById(R.id.gridOpciones)
        val tvTitulo = findViewById<TextView>(R.id.tvTituloGestion)
        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenidaAdmin)
        val tvFechaHora = findViewById<TextView>(R.id.tvFechaHora)
        val btnSalir = findViewById<Button>(R.id.btnSalirGestion)

        // Mostrar texto estático
        tvTitulo.text = "Gestión Académica"
        tvBienvenida.text = "Bienvenido, Administrador del sistema"

        // Mostrar hora actual dinámica
        val handler = Handler(Looper.getMainLooper())
        val updateTimeRunnable = object : Runnable {
            override fun run() {
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                sdf.timeZone = TimeZone.getTimeZone("America/Lima")
                val currentTime = sdf.format(Date())
                tvFechaHora.text = "Fecha y hora: $currentTime"
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(updateTimeRunnable)

        // Adaptador y clicks del GridView
        opcionesAdapter = OpcionGestionAdapter(this, opciones)
        gridView.adapter = opcionesAdapter

        gridView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> startActivity(Intent(this, RegistroCalificacionActivity::class.java))
                1 -> startActivity(Intent(this, RegistroAsistenciaActivity::class.java))
                2 -> startActivity(Intent(this, CursosHorariosActivity::class.java))
                3 -> startActivity(Intent(this, VerAsistenciasActivity::class.java))
                else -> Toast.makeText(this, "Opción en desarrollo", Toast.LENGTH_SHORT).show()
            }
        }

        btnSalir.setOnClickListener {
            startActivity(Intent(this, DrawerBaseActivity::class.java))
            finish()
        }
    }
}
