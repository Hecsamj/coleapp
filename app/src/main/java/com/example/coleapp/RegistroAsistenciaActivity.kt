package com.example.coleapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class RegistroAsistenciaActivity : AppCompatActivity() {

    private lateinit var spinnerNivel: Spinner
    private lateinit var spinnerGrado: Spinner
    private lateinit var btnCargarAlumnos: Button
    private lateinit var recyclerViewAsistencia: RecyclerView
    private lateinit var btnGuardarAsistencia: Button
    private lateinit var btnVolverDrawer: Button

    private lateinit var adapter: AlumnoAsistenciaAdapter

    private val db = FirebaseFirestore.getInstance()
    private val niveles = listOf("Primaria", "Secundaria")
    private val gradosPrimaria = listOf("1ro", "2do", "3ro", "4to", "5to", "6to")
    private val gradosSecundaria = listOf("1ro", "2do", "3ro", "4to", "5to")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_asistencia)

        spinnerNivel = findViewById(R.id.spinnerNivel)
        spinnerGrado = findViewById(R.id.spinnerGrado)
        btnCargarAlumnos = findViewById(R.id.btnCargarAlumnos)
        recyclerViewAsistencia = findViewById(R.id.recyclerViewAsistencia)
        btnGuardarAsistencia = findViewById(R.id.btnGuardarAsistencia)
        btnVolverDrawer = findViewById(R.id.btnVolverDrawer)

        val nivelAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, niveles)
        nivelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerNivel.adapter = nivelAdapter

        spinnerNivel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long
            ) {
                val nivelSeleccionado = niveles[position]
                val grados = if (nivelSeleccionado == "Primaria") gradosPrimaria else gradosSecundaria

                val gradoAdapter = ArrayAdapter(
                    this@RegistroAsistenciaActivity,
                    android.R.layout.simple_spinner_item,
                    grados
                )
                gradoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerGrado.adapter = gradoAdapter
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnCargarAlumnos.setOnClickListener { cargarAlumnos() }

        btnGuardarAsistencia.setOnClickListener { guardarAsistencias() }

        btnVolverDrawer.setOnClickListener {
            startActivity(Intent(this, DrawerBaseActivity::class.java))
            finish()
        }
    }

    private fun cargarAlumnos() {
        val nivel = spinnerNivel.selectedItem.toString()
        val grado = spinnerGrado.selectedItem.toString()

        db.collection("matriculas")
            .whereEqualTo("nivel", nivel)
            .whereEqualTo("grado", grado)
            .get()
            .addOnSuccessListener { documentos ->
                val listaAlumnos = documentos.map { it.data }
                adapter = AlumnoAsistenciaAdapter(listaAlumnos)
                recyclerViewAsistencia.layoutManager = LinearLayoutManager(this)
                recyclerViewAsistencia.adapter = adapter
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar alumnos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun guardarAsistencias() {
        val asistencias = adapter.obtenerAsistencias()
        val fecha = System.currentTimeMillis().toString()

        for (asistencia in asistencias) {
            val asistenciaMap = hashMapOf(
                "correo" to asistencia.correo,
                "nombre" to asistencia.nombre,
                "nivel" to asistencia.nivel,
                "grado" to asistencia.grado,
                "asistio" to asistencia.asistio,
                "fecha" to fecha
            )

            db.collection("asistencias").add(asistenciaMap)
        }

        Toast.makeText(this, "Asistencias guardadas correctamente", Toast.LENGTH_SHORT).show()
    }
}
