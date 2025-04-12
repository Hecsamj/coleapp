package com.example.coleapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AlumnoActivity : AppCompatActivity() {

    private lateinit var spinnerNivel: Spinner
    private lateinit var spinnerGrado: Spinner
    private lateinit var btnActualizarCursos: Button
    private lateinit var contenedorCursos: LinearLayout
    private lateinit var tvNombre: TextView
    private lateinit var tvMatricula: TextView
    private lateinit var tvGrado: TextView
    private lateinit var tvCorreo: TextView
    private lateinit var tvApellidoPaterno: TextView  // NUEVO
    private lateinit var tvApellidoMaterno: TextView  // NUEVO
    private lateinit var tvColegioProcedencia: TextView  // NUEVO
    private lateinit var tvFechaNacimiento: TextView  // NUEVO
    private lateinit var imgFoto: ImageView
    private lateinit var btnCerrarSesion: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alumno)

        // Inicialización de las vistas
        spinnerNivel = findViewById(R.id.spinnerNivel)
        spinnerGrado = findViewById(R.id.spinnerGrado)
        btnActualizarCursos = findViewById(R.id.btnActualizarCursos)
        contenedorCursos = findViewById(R.id.contenedorCursos)
        tvNombre = findViewById(R.id.tvNombre)
        tvMatricula = findViewById(R.id.tvMatricula)
        tvGrado = findViewById(R.id.tvGrado)
        tvCorreo = findViewById(R.id.tvCorreo) // Campo para mostrar el correo
        tvApellidoPaterno = findViewById(R.id.tvApellidoPaterno)  // NUEVO
        tvApellidoMaterno = findViewById(R.id.tvApellidoMaterno)  // NUEVO
        tvColegioProcedencia = findViewById(R.id.tvColegioProcedencia)  // NUEVO
        tvFechaNacimiento = findViewById(R.id.tvFechaNacimiento)  // NUEVO
        imgFoto = findViewById(R.id.imgFoto)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)

        // Obtener datos de matrícula
        obtenerDatosMatricula()

        // Configurar Spinners
        val niveles = arrayOf("Primaria", "Secundaria")
        val gradosPrimaria = arrayOf("1ro", "2do", "3ro", "4to", "5to", "6to")
        val gradosSecundaria = arrayOf("1ro", "2do", "3ro", "4to", "5to")

        val adapterNivel = ArrayAdapter(this, android.R.layout.simple_spinner_item, niveles)
        adapterNivel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerNivel.adapter = adapterNivel

        // Configurar el listener para el Spinner de nivel
        spinnerNivel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, view: View?, position: Int, id: Long) {
                val grados = if (spinnerNivel.selectedItem == "Primaria") gradosPrimaria else gradosSecundaria
                val adapterGrado = ArrayAdapter(this@AlumnoActivity, android.R.layout.simple_spinner_item, grados)
                adapterGrado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerGrado.adapter = adapterGrado
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // Nada que hacer si no se selecciona nada
            }
        }

        // Configurar botón para actualizar cursos
        btnActualizarCursos.setOnClickListener {
            actualizarCursos()
        }

        // Configurar el botón de cerrar sesión
        btnCerrarSesion.setOnClickListener {
            cerrarSesion()
        }
    }

    private fun mostrarDatosEnDialog(nombres: String, apellidoPaterno: String, apellidoMaterno: String, matricula: String, grado: String, correo: String, colegioProcedencia: String, fechaNacimiento: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Detalles de Matrícula")
            .setMessage("Nombre: $nombres\nApellido Paterno: $apellidoPaterno\nApellido Materno: $apellidoMaterno\nMatrícula: $matricula\nGrado: $grado\nCorreo: $correo\nColegio de Procedencia: $colegioProcedencia\nFecha de Nacimiento: $fechaNacimiento")
            .setPositiveButton("Aceptar", null)
            .show()
    }


    private fun obtenerDatosMatricula() {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email
        if (userEmail == null) {
            Toast.makeText(this, "No hay un usuario logueado", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("UserEmail", "Correo del usuario logueado: $userEmail") // Verificar el correo del usuario

        val db = FirebaseFirestore.getInstance()
        val matriculaRef = db.collection("Matriculas").document(userEmail)

        Log.d("Firestore", "Referencia de Firestore: $matriculaRef") // Verificar la referencia al documento en Firestore

        matriculaRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                Log.d("Firestore", "Documento encontrado con éxito")

                // Obtener los campos del documento
                val nombre = document.getString("nombre") ?: "Nombre no disponible"
                val matricula = document.getString("matricula") ?: "Matrícula no disponible"
                val grado = document.getString("grado") ?: "Grado no disponible"
                val correo = document.getString("correo") ?: "Correo no disponible"
                val apellidoPaterno = document.getString("apellidoPaterno") ?: "Apellido Paterno no disponible"
                val apellidoMaterno = document.getString("apellidoMaterno") ?: "Apellido Materno no disponible"
                val colegioProcedencia = document.getString("colegioProcedencia") ?: "Colegio no disponible"
                val fechaNacimiento = document.getString("fechaNacimiento") ?: "Fecha de Nacimiento no disponible"

                // Mostrar los datos en los logs para verificar que estamos obteniendo la información correctamente
                Log.d("Firestore", "Datos obtenidos: $nombre, $matricula, $grado, $correo")

                // Actualizar la UI con los datos obtenidos
                tvNombre.text = "Nombre: $nombre"
                tvMatricula.text = "Matrícula: $matricula"
                tvGrado.text = "Grado: $grado"
                tvCorreo.text = "Correo: $correo"
                tvApellidoPaterno.text = "Apellido Paterno: $apellidoPaterno"
                tvApellidoMaterno.text = "Apellido Materno: $apellidoMaterno"
                tvColegioProcedencia.text = "Colegio de Procedencia: $colegioProcedencia"
                tvFechaNacimiento.text = "Fecha de Nacimiento: $fechaNacimiento"
            } else {
                Log.d("Firestore", "Documento no existe")
                Toast.makeText(this, "No se encontraron datos de matrícula", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Log.e("Firestore", "Error al obtener documento: ${e.message}")
            Toast.makeText(this, "Error al obtener datos de matrícula", Toast.LENGTH_SHORT).show()
        }
    }


    private fun actualizarCursos() {
        val nivelSeleccionado = spinnerNivel.selectedItem.toString()
        val gradoSeleccionado = spinnerGrado.selectedItem.toString()

        val coleccion = if (nivelSeleccionado == "Primaria") "CursosPrimaria" else "CursosSecundaria"

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection(coleccion).document(gradoSeleccionado)

        docRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val cursos = document.get("cursos") as? List<String> ?: emptyList()
                val horarios = document.get("horarios") as? List<String> ?: emptyList()
                mostrarCursosEnUI(cursos, horarios)
            } else {
                Toast.makeText(this, "No se encontraron datos", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Error al obtener cursos", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun mostrarCursosEnUI(cursos: List<String>, horarios: List<String>) {
        contenedorCursos.removeAllViews() // Limpia la lista antes de actualizar

        for (i in cursos.indices) {
            val curso = cursos[i]
            val horario = horarios.getOrNull(i) ?: "Horario no disponible"

            val textView = TextView(this).apply {
                text = "$curso - $horario"
                textSize = 16f
                setPadding(8, 8, 8, 8)
            }

            contenedorCursos.addView(textView)
        }
    }

    private fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut() // Cerrar sesión en Firebase
        val intent = Intent(this, DrawerBaseActivity::class.java) // Redirigir al Login
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish() // Finalizar la actividad actual
    }
}
