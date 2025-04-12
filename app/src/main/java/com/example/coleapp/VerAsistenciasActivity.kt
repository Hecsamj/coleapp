package com.example.coleapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class VerAsistenciasActivity : AppCompatActivity() {

    private lateinit var spinnerNivel: Spinner
    private lateinit var spinnerGrado: Spinner
    private lateinit var btnCargar: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnVolver: Button

    private val db = FirebaseFirestore.getInstance()

    private val niveles = listOf("Primaria", "Secundaria")
    private val gradosPrimaria = listOf("1ro", "2do", "3ro", "4to", "5to", "6to")
    private val gradosSecundaria = listOf("1ro", "2do", "3ro", "4to", "5to")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_asistencias)

        spinnerNivel = findViewById(R.id.spinnerNivel)
        spinnerGrado = findViewById(R.id.spinnerGrado)
        btnCargar = findViewById(R.id.btnCargarAsistencias)
        recyclerView = findViewById(R.id.recyclerViewVerAsistencias)
        btnVolver = findViewById(R.id.btnVolverMenu)

        val adapterNivel = ArrayAdapter(this, android.R.layout.simple_spinner_item, niveles)
        adapterNivel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerNivel.adapter = adapterNivel

        spinnerNivel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long
            ) {
                val grados = if (niveles[position] == "Primaria") gradosPrimaria else gradosSecundaria
                val adapterGrado = ArrayAdapter(
                    this@VerAsistenciasActivity,
                    android.R.layout.simple_spinner_item,
                    grados
                )
                adapterGrado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerGrado.adapter = adapterGrado
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnCargar.setOnClickListener { cargarAsistencias() }

        btnVolver.setOnClickListener {
            startActivity(Intent(this, DrawerBaseActivity::class.java))
            finish()
        }
    }

    private fun cargarAsistencias() {
        val nivelSeleccionado = spinnerNivel.selectedItem.toString()
        val gradoSeleccionado = spinnerGrado.selectedItem.toString()

        db.collection("asistencias")
            .whereEqualTo("nivel", nivelSeleccionado)
            .whereEqualTo("grado", gradoSeleccionado)
            .get()
            .addOnSuccessListener { documentos ->
                val listaAsistencias = documentos.mapNotNull { doc ->
                    val correo = doc.getString("correo") ?: return@mapNotNull null
                    val nombre = doc.getString("nombre") ?: return@mapNotNull null
                    val nivel = doc.getString("nivel") ?: return@mapNotNull null
                    val grado = doc.getString("grado") ?: return@mapNotNull null
                    val asistio = doc.getBoolean("asistio") ?: false

                    AlumnoAsistencia(correo, nombre, nivel, grado, asistio)
                }

                val adapter = VerAsistenciasAdapter(listaAsistencias)
                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.adapter = adapter
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar asistencias", Toast.LENGTH_SHORT).show()
            }
    }
}
