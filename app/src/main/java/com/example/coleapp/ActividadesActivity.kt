package com.example.coleapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ActividadesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ActividadAdapter
    private lateinit var progressBar: View
    private lateinit var btnVolver: Button
    private val actividadesList = mutableListOf<Actividad>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actividades)

        recyclerView = findViewById(R.id.rvActividades)
        progressBar = findViewById(R.id.progressBar)
        btnVolver = findViewById(R.id.btnVolver) // Agregar referencia al botón

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ActividadAdapter(actividadesList)
        recyclerView.adapter = adapter

        cargarActividadesDesdeFirestore()

        // Configurar botón "Volver"
        btnVolver.setOnClickListener {
            val intent = Intent(this, DrawerBaseActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // Evita apilar actividades
            startActivity(intent)
            finish() // Cierra la actividad actual
        }
    }

    private fun cargarActividadesDesdeFirestore() {
        progressBar.visibility = View.VISIBLE // Mostrar ProgressBar

        val db = FirebaseFirestore.getInstance()
        db.collection("actividades")
            .get()
            .addOnSuccessListener { result ->
                actividadesList.clear()
                for (document in result) {
                    val actividad = document.toObject(Actividad::class.java)
                    actividadesList.add(actividad)
                }
                adapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE // Ocultar ProgressBar
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error obteniendo actividades", exception)
                progressBar.visibility = View.GONE // Ocultar ProgressBar
            }
    }
}
