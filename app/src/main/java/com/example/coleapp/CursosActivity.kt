package com.example.coleapp

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Spinner


class CursosActivity : AppCompatActivity() {

    private lateinit var spinnerGrado: Spinner
    private lateinit var contenedorCursos: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cursos)

        // Inicializar vistas
        spinnerGrado = findViewById(R.id.spinnerGrado)
        contenedorCursos = findViewById(R.id.contenedorCursos)

        // Llenar el Spinner con los grados
        val grados = arrayOf(
            "1ro de Primaria",
            "2do de Primaria",
            "3ro de Primaria",
            "4to de Primaria",
            "5to de Primaria",
            "6to de Primaria",
            "1ro de Secundaria",
            "2do de Secundaria",
            "3ro de Secundaria",
            "4to de Secundaria",
            "5to de Secundaria"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, grados)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGrado.adapter = adapter

        // Detectar selección en el Spinner
        spinnerGrado.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val gradoSeleccionado = grados[position]
                cargarCursosDesdeFirebase(gradoSeleccionado)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // No hacer nada
            }
        }
    }

    // Función para cargar los cursos desde Firebase según el grado seleccionado
    private fun cargarCursosDesdeFirebase(grado: String) {
        val db = FirebaseFirestore.getInstance()
        val collectionPath =
            if (grado.contains("Primaria")) "CursosPrimaria" else "CursosSecundaria"

        db.collection(collectionPath).document(grado)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val listaCursos =
                        document.get("cursos") as? List<Map<String, Any>> ?: emptyList()

                    contenedorCursos.removeAllViews()  // Limpiar los cursos previamente mostrados

                    for (cursoMap in listaCursos) {
                        val curso = cursoMap["curso"] as String
                        val horarios = (cursoMap["horarios"] as List<Map<String, Any>>)

                        // Crear un TextView para cada curso
                        val textViewCurso = TextView(this)
                        textViewCurso.text = "Curso: $curso"
                        textViewCurso.textSize = 18f
                        textViewCurso.setPadding(0, 10, 0, 5)

                        // Añadir los horarios al TextView del curso
                        horarios.forEach { horarioMap ->
                            val horaInicio = horarioMap["horaInicio"] as String
                            val horaFin = horarioMap["horaFin"] as String
                            val dias = horarioMap["dias"] as List<String>

                            val horariosText = "Horario: $horaInicio - $horaFin, Días: ${dias.joinToString(", ")}"
                            val textViewHorario = TextView(this)
                            textViewHorario.text = horariosText
                            textViewHorario.textSize = 16f
                            textViewHorario.setPadding(20, 5, 0, 10)

                            contenedorCursos.addView(textViewCurso)  // Agregar el curso
                            contenedorCursos.addView(textViewHorario)  // Agregar los horarios
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    "Error al cargar los cursos: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
