package com.example.coleapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class NoticiasActivity : DrawerBaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NoticiaAdapter
    private lateinit var progressBar: View
    private lateinit var btnVolver: Button
    private val noticiasList = mutableListOf<Noticia>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflar el contenido dentro del drawer
        val inflater = LayoutInflater.from(this)
        val contentView = inflater.inflate(R.layout.activity_noticias, null)
        setContentViewToDrawer(contentView)

        recyclerView = findViewById(R.id.rvNoticias)
        progressBar = findViewById(R.id.progressBarNoticias)
        btnVolver = findViewById(R.id.btnVolverNoticias)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = NoticiaAdapter(noticiasList)
        recyclerView.adapter = adapter

        cargarNoticiasDesdeFirestore()

        btnVolver.setOnClickListener {
            finish() // Simplemente cierra esta actividad y vuelve al anterior
        }
    }

    private fun cargarNoticiasDesdeFirestore() {
        progressBar.visibility = View.VISIBLE

        val db = FirebaseFirestore.getInstance()
        db.collection("noticias")
            .get()
            .addOnSuccessListener { result ->
                noticiasList.clear()
                for (document in result) {
                    val noticia = Noticia(
                        titulo = document.getString("titulo") ?: "Sin título",
                        descripcion = document.getString("descripcion") ?: "Sin descripción",
                        imagenUrl = document.getString("imagenUrl") ?: "",
                        categoria = document.getString("categoria") ?: "General",
                        fecha = document.getString("fecha") ?: "Fecha desconocida"
                    )
                    noticiasList.add(noticia)
                }
                adapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error obteniendo noticias", exception)
                progressBar.visibility = View.GONE
            }
    }
}
