package com.example.coleapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class RegistroCalificacionActivity : AppCompatActivity() {

    private lateinit var spinnerAlumno: Spinner
    private lateinit var spinnerCurso: Spinner
    private lateinit var editTextNota: EditText
    private lateinit var btnRegistrarCalificacion: Button
    private lateinit var btnVerCalificaciones: Button

    private val db = FirebaseFirestore.getInstance()

    private var listaAlumnos = mutableListOf<Map<String, Any>>()
    private var listaCursos = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_calificacion)

        spinnerAlumno = findViewById(R.id.spinnerAlumno)
        spinnerCurso = findViewById(R.id.spinnerCurso)
        editTextNota = findViewById(R.id.editTextNota)
        btnRegistrarCalificacion = findViewById(R.id.btnRegistrarCalificacion)
        btnVerCalificaciones = findViewById(R.id.btnVerCalificaciones)

        cargarAlumnos()

        spinnerAlumno.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                val alumno = listaAlumnos[position]
                val grado = alumno["grado"].toString()
                val nivel = alumno["nivel"].toString()
                cargarCursos(grado, nivel)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        btnRegistrarCalificacion.setOnClickListener {
            registrarCalificacion()
        }

        btnVerCalificaciones.setOnClickListener {
            startActivity(Intent(this, VerCalificacionesActivity::class.java))
        }
    }

    private fun cargarAlumnos() {
        db.collection("matriculas").get().addOnSuccessListener { documents ->
            listaAlumnos.clear()
            val nombresAlumnos = mutableListOf<String>()

            for (doc in documents) {
                val data = doc.data
                data["correo"] = doc.id
                listaAlumnos.add(data)

                val nombreCompleto = "${data["nombre"]} ${data["apellidoPaterno"]} ${data["apellidoMaterno"]} - ${data["nivel"]} ${data["grado"]}"
                nombresAlumnos.add(nombreCompleto)
            }

            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombresAlumnos)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerAlumno.adapter = adapter
        }.addOnFailureListener {
            Toast.makeText(this, "Error al cargar alumnos: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarCursos(grado: String, nivel: String) {
        val coleccion = if (nivel.equals("Primaria", ignoreCase = true)) "CursosPrimaria" else "CursosSecundaria"

        db.collection(coleccion).document(grado).get().addOnSuccessListener { document ->
            val cursos = document.get("cursos") as? List<*>
            if (cursos != null) {
                listaCursos = cursos.filterIsInstance<String>().toMutableList()
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listaCursos)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerCurso.adapter = adapter
            } else {
                listaCursos.clear()
                spinnerCurso.adapter = null
                Toast.makeText(this, "No hay cursos disponibles para $grado - $nivel", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error al cargar cursos: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registrarCalificacion() {
        val notaStr = editTextNota.text.toString()
        if (notaStr.isBlank()) {
            Toast.makeText(this, "Ingrese la nota", Toast.LENGTH_SHORT).show()
            return
        }

        val nota = notaStr.toDoubleOrNull()
        if (nota == null || nota < 0 || nota > 20) {
            Toast.makeText(this, "Nota inválida. Debe estar entre 0 y 20.", Toast.LENGTH_SHORT).show()
            return
        }

        if (spinnerCurso.adapter == null || spinnerCurso.selectedItem == null) {
            Toast.makeText(this, "Primero seleccione un curso válido", Toast.LENGTH_SHORT).show()
            return
        }

        val posicion = spinnerAlumno.selectedItemPosition
        val alumno = listaAlumnos[posicion]
        val nombreAlumno = "${alumno["nombre"]} ${alumno["apellidoPaterno"]} ${alumno["apellidoMaterno"]}"
        val correoAlumno = alumno["correo"].toString()
        val curso = spinnerCurso.selectedItem.toString()
        val grado = alumno["grado"].toString()
        val nivel = alumno["nivel"].toString()

        val calificacion = hashMapOf(
            "alumnoCorreo" to correoAlumno,
            "alumnoNombre" to nombreAlumno,
            "curso" to curso,
            "grado" to grado,
            "nivel" to nivel,
            "nota" to nota
        )

        db.collection("calificaciones").add(calificacion)
            .addOnSuccessListener {
                Toast.makeText(this, "Calificación registrada correctamente", Toast.LENGTH_SHORT).show()
                editTextNota.text.clear()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al registrar: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
