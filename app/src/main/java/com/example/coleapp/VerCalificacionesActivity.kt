package com.example.coleapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class VerCalificacionesActivity : AppCompatActivity() {

    private lateinit var spinnerGrado: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnVolver: Button
    private val db = FirebaseFirestore.getInstance()
    private val listaCalificaciones = mutableListOf<Calificacion>()
    private lateinit var adapter: CalificacionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_calificaciones)

        spinnerGrado = findViewById(R.id.spinnerGrado)
        recyclerView = findViewById(R.id.recyclerViewCalificaciones)
        btnVolver = findViewById(R.id.btnVolverDrawer)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CalificacionAdapter()
        recyclerView.adapter = adapter

        val grados = listOf("1ro", "2do", "3ro", "4to", "5to", "6to")
        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, grados)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGrado.adapter = adapterSpinner

        spinnerGrado.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val gradoSeleccionado = grados[position]
                cargarCalificacionesPorGrado(gradoSeleccionado)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        btnVolver.setOnClickListener {
            startActivity(Intent(this, GestionAcademicaActivity::class.java))
            finish()
        }
    }

    private fun cargarCalificacionesPorGrado(grado: String) {
        db.collection("calificaciones")
            .whereEqualTo("grado", grado)
            .get()
            .addOnSuccessListener { documentos ->
                listaCalificaciones.clear()
                for (doc in documentos) {
                    val data = doc.data
                    val calificacion = Calificacion(
                        alumnoNombre = data["alumnoNombre"].toString(),
                        curso = data["curso"].toString(),
                        nota = data["nota"].toString().toDoubleOrNull() ?: 0.0,
                        grado = data["grado"].toString(),
                        nivel = data["nivel"].toString()
                    )
                    listaCalificaciones.add(calificacion)
                }
                adapter.actualizarDatos(listaCalificaciones)
            }
    }
}
