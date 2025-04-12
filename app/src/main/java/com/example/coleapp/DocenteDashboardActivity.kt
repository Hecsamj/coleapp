package com.example.coleapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DocenteDashboardActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var tvBienvenida: TextView
    private lateinit var tvMensaje: TextView
    private lateinit var rvCursos: RecyclerView
    private lateinit var btnCerrarSesion: Button
    private lateinit var btnAgregarCurso: Button

    private lateinit var cursoAdapter: CursoAdapter
    private val listaCursos = mutableListOf<Curso>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_docente_dashboard)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Referencias a la UI
        tvBienvenida = findViewById(R.id.tvBienvenidaDocente)
        tvMensaje = findViewById(R.id.tvMensaje)
        rvCursos = findViewById(R.id.rvCursos)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)
        btnAgregarCurso = findViewById(R.id.btnAgregarCurso)

        // Configurar RecyclerView
        cursoAdapter = CursoAdapter(listaCursos)
        rvCursos.layoutManager = LinearLayoutManager(this)
        rvCursos.adapter = cursoAdapter

        // Cargar datos del docente desde Firestore
        cargarDatosDocente()

        // Botón cerrar sesión
        btnCerrarSesion.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, DocenteActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Botón agregar curso
        btnAgregarCurso.setOnClickListener {
            // Lógica para agregar un curso (por ejemplo, abrir una nueva actividad)
            val intent = Intent(this, DocenteDashboardActivity::class.java)
            startActivity(intent)
        }
    }

    private fun cargarDatosDocente() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("Docentes").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val nombres = document.getString("nombres") ?: "Nombres no encontrados"
                    val apellidos = document.getString("apellidos") ?: "Apellidos no encontrados"
                    val codigoDocente = document.getString("codigo_docente")

                    tvBienvenida.text = "Bienvenido, $nombres $apellidos"

                    if (codigoDocente.isNullOrEmpty()) {
                        Log.e("Firestore", "Código docente no encontrado")
                        tvMensaje.text = "Código no disponible"
                        return@addOnSuccessListener
                    }

                    // Cargar cursos con el código del docente
                    cargarCursosPorCodigo(codigoDocente)
                } else {
                    Toast.makeText(this, "No se encontraron datos del docente", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .addOnFailureListener {
                Log.e("Firestore", "Error al obtener datos del docente", it)
                Toast.makeText(this, "Error al obtener datos del docente", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun cargarCursosPorCodigo(codigoDocente: String) {
        db.collection("Cursos")
            .whereEqualTo("codigo_docente", codigoDocente)
            .get()
            .addOnSuccessListener { documents ->
                listaCursos.clear()

                for (document in documents) {
                    val nombreCurso = document.getString("nombre") ?: ""

                    // Obtener la lista de horarios como un array de mapas
                    val horariosList =
                        document.get("horarios") as? List<Map<String, Any>> ?: emptyList()

                    // Convertir cada item en la lista de horarios a un objeto Horario
                    val horarios = horariosList.map { horarioMap ->
                        val horaInicio = horarioMap["horaInicio"] as? String ?: ""
                        val horaFin = horarioMap["horaFin"] as? String ?: ""
                        val dias = horarioMap["dias"] as? List<String> ?: emptyList()
                        Horario(horaInicio, horaFin, dias)
                    }

                    // Agregar los cursos con la lista de horarios
                    listaCursos.add(Curso(nombreCurso, horarios))
                }

                if (listaCursos.isEmpty()) {
                    tvMensaje.text = "No tienes cursos asignados"
                    rvCursos.visibility = View.GONE
                } else {
                    tvMensaje.text = ""
                    rvCursos.visibility = View.VISIBLE
                    cursoAdapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener {
                Log.e("Firestore", "Error al obtener cursos", it)
                Toast.makeText(this, "Error al obtener cursos", Toast.LENGTH_SHORT).show()
            }
    }
}
