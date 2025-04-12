package com.example.coleapp

import Docente
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class DocenteActivity : DrawerBaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var docenteAdapter: DocenteAdapter
    private lateinit var progressBar: ProgressBar
    private val db = FirebaseFirestore.getInstance()
    private val docentesList = mutableListOf<Docente>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contentView = layoutInflater.inflate(R.layout.activity_docente, null)
        setContentViewToDrawer(contentView)

        recyclerView = contentView.findViewById(R.id.recyclerDocentes)
        progressBar = contentView.findViewById(R.id.progressBarDocente)

        recyclerView.layoutManager = LinearLayoutManager(this)
        docenteAdapter = DocenteAdapter(docentesList)
        recyclerView.adapter = docenteAdapter

        obtenerDocentes()
    }

    private fun obtenerDocentes() {
        progressBar.visibility = View.VISIBLE

        db.collection("docentes")
            .get()
            .addOnSuccessListener { result ->
                docentesList.clear()
                for (document in result) {
                    val docente = document.toObject(Docente::class.java)
                    docentesList.add(docente)
                }
                docenteAdapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al cargar docentes", Toast.LENGTH_SHORT).show()
                Log.e("DocenteActivity", "Error: ", exception)
                progressBar.visibility = View.GONE
            }
    }
}
