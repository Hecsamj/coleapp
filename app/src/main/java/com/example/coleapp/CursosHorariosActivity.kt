package com.example.coleapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class CursosHorariosActivity : AppCompatActivity() {

    private lateinit var spinnerNivel: Spinner
    private lateinit var spinnerGrado: Spinner
    private lateinit var btnCargarCursos: Button
    private lateinit var btnAgregarCurso: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnVolver: Button

    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: CursoHorarioAdapter

    private val niveles = listOf("Primaria", "Secundaria")
    private val gradosPrimaria = listOf("1ro", "2do", "3ro", "4to", "5to", "6to")
    private val gradosSecundaria = listOf("1ro", "2do", "3ro", "4to", "5to")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cursos_horarios)

        btnAgregarCurso = findViewById(R.id.btnAgregarCurso)

        spinnerNivel = findViewById(R.id.spinnerNivel)
        spinnerGrado = findViewById(R.id.spinnerGrado)
        btnCargarCursos = findViewById(R.id.btnCargarCursos)
        recyclerView = findViewById(R.id.recyclerViewCursos)
        btnVolver = findViewById(R.id.btnVolverDrawer)

        db = FirebaseFirestore.getInstance()

        val nivelAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, niveles)
        nivelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerNivel.adapter = nivelAdapter

        spinnerNivel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                val grados = if (niveles[position] == "Primaria") gradosPrimaria else gradosSecundaria
                val gradoAdapter = ArrayAdapter(this@CursosHorariosActivity, android.R.layout.simple_spinner_item, grados)
                gradoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerGrado.adapter = gradoAdapter
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        btnCargarCursos.setOnClickListener {
            val nivel = spinnerNivel.selectedItem.toString()
            val grado = spinnerGrado.selectedItem.toString()


            btnAgregarCurso.setOnClickListener {
                if (!::adapter.isInitialized) {
                    Toast.makeText(this, "Primero carga los cursos", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val dialogView = layoutInflater.inflate(R.layout.dialog_editar_curso, null)
                val edtCurso = dialogView.findViewById<EditText>(R.id.edtCurso)
                val edtHorario = dialogView.findViewById<EditText>(R.id.edtHorario)

                AlertDialog.Builder(this)
                    .setTitle("Agregar nuevo curso")
                    .setView(dialogView)
                    .setPositiveButton("Agregar") { dialog, _ ->
                        val curso = edtCurso.text.toString().trim()
                        val horario = edtHorario.text.toString().trim()
                        if (curso.isNotEmpty() && horario.isNotEmpty()) {
                            adapter.agregarCurso(CursoHorario(curso, horario))
                        } else {
                            Toast.makeText(this, "Completa ambos campos", Toast.LENGTH_SHORT).show()
                        }
                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
                    .show()
            }


            val coleccion = if (nivel == "Primaria") "CursosPrimaria" else "CursosSecundaria"

            db.collection(coleccion).document(grado)
                .get()
                .addOnSuccessListener { documento ->
                    if (documento != null && documento.exists()) {
                        val listaCursos = mutableListOf<CursoHorario>()
                        val cursos = documento.get("cursos") as? List<String> ?: emptyList()
                        val horarios = documento.get("horarios") as? List<String> ?: emptyList()

                        for (i in cursos.indices) {
                            val curso = cursos[i]
                            val horario = if (i < horarios.size) horarios[i] else "No definido"
                            listaCursos.add(CursoHorario(curso, horario))
                        }

                        adapter = CursoHorarioAdapter(listaCursos, nivel, grado)
                        recyclerView.layoutManager = LinearLayoutManager(this)
                        recyclerView.adapter = adapter
                    } else {
                        Toast.makeText(this, "No se encontraron cursos", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show()
                }
        }



        btnVolver.setOnClickListener {
            startActivity(Intent(this, GestionAcademicaActivity::class.java))
            finish()
        }
    }
}
