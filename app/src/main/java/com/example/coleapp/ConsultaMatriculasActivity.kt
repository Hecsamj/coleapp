package com.example.coleapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.example.coleapp.adapters.MatriculaAdapter
import com.example.coleapp.models.Matricula

class ConsultaMatriculasActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val matriculasList = mutableListOf<Matricula>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consulta_matriculas) // Asegúrate de que esta línea esté correcta

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        obtenerMatriculas()
    }

    private fun obtenerMatriculas() {
        db.collection("matriculas")
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(this, "No hay matrículas registradas", Toast.LENGTH_SHORT).show()
                } else {
                    for (document in documents) {
                        val nombre = document.getString("nombres_completos") ?: ""
                        val apellidos = document.getString("apellidos") ?: ""
                        val fechaNacimiento = document.getString("fecha_nacimiento") ?: ""
                        val grado = document.getString("grado") ?: ""
                        val foto = document.getString("foto") ?: ""  // La URL de la foto
                        val colegioProcedencia = document.getString("colegio_procedencia")

                        // Agregar la matrícula a la lista
                        matriculasList.add(Matricula(
                            id = document.id,
                            nombresCompletos = nombre,
                            apellidos = apellidos,
                            fechaNacimiento = fechaNacimiento,
                            grado = grado,
                            foto = foto,
                            colegioProcedencia = colegioProcedencia
                        ))
                    }

                    // Configurar el adaptador del RecyclerView
                    val adapter = MatriculaAdapter(matriculasList)
                    recyclerView.adapter = adapter
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al obtener las matrículas: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
