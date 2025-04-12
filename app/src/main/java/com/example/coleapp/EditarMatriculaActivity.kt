package com.example.coleapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class EditarMatriculaActivity : AppCompatActivity() {

    private lateinit var etNombreEstudiante: EditText
    private lateinit var etCurso: EditText
    private lateinit var etAnio: EditText
    private lateinit var btnActualizarMatricula: Button
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_matricula) // Asegúrate de que el archivo XML correcto está aquí

        // Inicialización de las vistas
        etNombreEstudiante = findViewById(R.id.etNombreEstudiante)
        etCurso = findViewById(R.id.etCurso)
        etAnio = findViewById(R.id.etAnio)
        btnActualizarMatricula = findViewById(R.id.btnActualizarMatricula)

        val matriculaId =
            intent.getStringExtra("matriculaId") // Obtener el ID de la matrícula desde el Intent

        // Aquí debes cargar los datos actuales de la matrícula para editar
        if (matriculaId != null) {
            cargarDatosMatricula(matriculaId)
        }

        btnActualizarMatricula.setOnClickListener {
            if (matriculaId != null) {
                actualizarMatricula(matriculaId)
            }
        }
    }

    private fun cargarDatosMatricula(matriculaId: String) {
        db.collection("matriculas").document(matriculaId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val nombre = document.getString("nombre")
                    val curso = document.getString("curso")
                    val anio = document.getString("anio")

                    etNombreEstudiante.setText(nombre)
                    etCurso.setText(curso)
                    etAnio.setText(anio)
                } else {
                    Toast.makeText(this, "Matrícula no encontrada", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun actualizarMatricula(matriculaId: String) {
        val nombre = etNombreEstudiante.text.toString()
        val curso = etCurso.text.toString()
        val anio = etAnio.text.toString()

        if (nombre.isNotEmpty() && curso.isNotEmpty() && anio.isNotEmpty()) {
            // Cambiar el tipo del HashMap a MutableMap<String, Any>
            val matriculaActualizada: MutableMap<String, Any> = hashMapOf(
                "nombre" to nombre,
                "curso" to curso,
                "anio" to anio
            )

            db.collection("matriculas").document(matriculaId)
                .update(matriculaActualizada)
                .addOnSuccessListener {
                    Toast.makeText(this, "Matrícula actualizada exitosamente", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al actualizar matrícula", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
        }
    }
}
